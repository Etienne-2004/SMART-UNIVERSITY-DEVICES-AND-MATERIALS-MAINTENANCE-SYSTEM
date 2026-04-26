package com.example.smart_university_devices_and_materials_maintanance_system.service;

import com.example.smart_university_devices_and_materials_maintanance_system.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NavigationService {

    private final UserService userService;
    private final DeviceService deviceService;
    private final MaterialService materialService;
    private final MaintenanceService maintenanceService;
    private final NotificationService notificationService;

    public static class NavigationItem {
        private String name;
        private String icon;
        private String url;
        private String badge;
        private boolean active;
        private String description;

        public NavigationItem(String name, String icon, String url, String badge, boolean active, String description) {
            this.name = name;
            this.icon = icon;
            this.url = url;
            this.badge = badge;
            this.active = active;
            this.description = description;
        }

        // Getters
        public String getName() { return name; }
        public String getIcon() { return icon; }
        public String getUrl() { return url; }
        public String getBadge() { return badge; }
        public boolean isActive() { return active; }
        public String getDescription() { return description; }
    }

    /**
     * Get smart navigation items based on user role and context
     */
    public List<NavigationItem> getSmartNavigationItems(User currentUser, String currentPath) {
        List<NavigationItem> items = new ArrayList<>();

        switch (currentUser.getRole()) {
            case ADMIN:
                items = getAdminNavigation(currentUser, currentPath);
                break;
            case TECHNICIAN:
                items = getTechnicianNavigation(currentUser, currentPath);
                break;
            case STAFF:
                items = getStaffNavigation(currentUser, currentPath);
                break;
            case CLEANER_STUDENT:
                items = getCleanerNavigation(currentUser, currentPath);
                break;
        }

        return items;
    }

    /**
     * Admin-specific navigation with contextual awareness
     */
    private List<NavigationItem> getAdminNavigation(User admin, String currentPath) {
        List<NavigationItem> items = new ArrayList<>();

        // Get real-time counts for badges
        int pendingUsers = userService.getPendingApprovals().size();
        int pendingDevices = deviceService.getPendingApproval().size();
        int pendingMaterials = materialService.getPendingApproval().size();
        long pendingTasks = maintenanceService.countByStatus(com.example.smart_university_devices_and_materials_maintanance_system.entities.MaintenanceRequest.TaskStatus.PENDING);
        long unreadNotifications = notificationService.countUnread(admin.getId());

        // Dashboard - Always first with notification badge
        items.add(new NavigationItem(
            "Dashboard", 
            "bi-speedometer2", 
            "/admin/dashboard", 
            unreadNotifications > 0 ? String.valueOf(unreadNotifications) : null,
            currentPath.contains("/admin/dashboard"),
            "System overview & alerts"
        ));

        // User Management - Shows pending count
        items.add(new NavigationItem(
            "User Management", 
            "bi-people-fill", 
            "/admin/users", 
            pendingUsers > 0 ? String.valueOf(pendingUsers) : null,
            currentPath.contains("/admin/users"),
            "Manage user accounts & approvals"
        ));

        // Device Management - Shows pending approvals
        items.add(new NavigationItem(
            "Device Control", 
            "bi-cpu-fill", 
            "/admin/devices", 
            pendingDevices > 0 ? String.valueOf(pendingDevices) : null,
            currentPath.contains("/admin/devices"),
            "Monitor & approve devices"
        ));

        // Material Management - Shows pending approvals
        items.add(new NavigationItem(
            "Materials Hub", 
            "bi-box-seam-fill", 
            "/admin/materials", 
            pendingMaterials > 0 ? String.valueOf(pendingMaterials) : null,
            currentPath.contains("/admin/materials"),
            "Manage learning materials"
        ));

        // Maintenance Operations - Shows pending tasks
        items.add(new NavigationItem(
            "Maintenance", 
            "bi-tools", 
            "/admin/maintenance", 
            pendingTasks > 0 ? String.valueOf(pendingTasks) : null,
            currentPath.contains("/admin/maintenance"),
            "Assign & track maintenance"
        ));

        // Analytics - System insights
        items.add(new NavigationItem(
            "Analytics", 
            "bi-graph-up", 
            "/admin/analytics", 
            null,
            currentPath.contains("/admin/analytics"),
            "Performance metrics"
        ));

        // Learning Centre - Educational resources
        items.add(new NavigationItem(
            "Learning", 
            "bi-book-fill", 
            "/learning", 
            null,
            currentPath.contains("/learning"),
            "Training resources"
        ));

        return items;
    }

    /**
     * Technician-specific navigation with admin-style structure
     */
    private List<NavigationItem> getTechnicianNavigation(User technician, String currentPath) {
        List<NavigationItem> items = new ArrayList<>();

        // Get real-time counts
        int assignedTasks = maintenanceService.getByTechnician(technician.getId()).size();
        long unreadNotifications = notificationService.countUnread(technician.getId());

        // Dashboard
        items.add(new NavigationItem(
            "Dashboard", 
            "bi-speedometer2", 
            "/technician/dashboard", 
            unreadNotifications > 0 ? String.valueOf(unreadNotifications) : null,
            currentPath.contains("/technician/dashboard"),
            "Technical operations overview"
        ));

        // My Tasks - The core work area
        items.add(new NavigationItem(
            "My Tasks", 
            "bi-tools", 
            "/technician/tasks", 
            assignedTasks > 0 ? String.valueOf(assignedTasks) : null,
            currentPath.contains("/technician/tasks"),
            "View & manage assigned maintenance"
        ));

        // Learning Centre - Training resources
        items.add(new NavigationItem(
            "Learning Centre", 
            "bi-mortarboard-fill", 
            "/learning", 
            null,
            currentPath.contains("/learning"),
            "Technical training & best practices"
        ));

        return items;
    }

    /**
     * Student-specific navigation focused on learning and requests
     */
    private List<NavigationItem> getStudentNavigation(User student, String currentPath) {
        List<NavigationItem> items = new ArrayList<>();

        int myRequests = maintenanceService.getMyRequests(student.getId()).size();
        long unreadNotifications = notificationService.countUnread(student.getId());

        items.add(new NavigationItem(
            "My Dashboard", 
            "bi-speedometer2", 
            "/student/dashboard", 
            unreadNotifications > 0 ? String.valueOf(unreadNotifications) : null,
            currentPath.contains("/student/dashboard"),
            "Personal overview"
        ));

        items.add(new NavigationItem(
            "My Requests", 
            "bi-clipboard", 
            "/student/requests", 
            myRequests > 0 ? String.valueOf(myRequests) : null,
            currentPath.contains("/student/requests"),
            "Track maintenance requests"
        ));

        items.add(new NavigationItem(
            "Request Service", 
            "bi-plus-circle", 
            "/student/request", 
            null,
            currentPath.contains("/student/request"),
            "New maintenance request"
        ));

        items.add(new NavigationItem(
            "Learning Centre", 
            "bi-book-fill", 
            "/learning", 
            null,
            currentPath.contains("/learning"),
            "Educational resources"
        ));

        items.add(new NavigationItem(
            "Device Catalog", 
            "bi-grid-3x3-gap", 
            "/student/devices", 
            null,
            currentPath.contains("/student/devices"),
            "Browse available devices"
        ));

        return items;
    }

    /**
     * Lecturer-specific navigation focused on teaching and resources
     */
    private List<NavigationItem> getLecturerNavigation(User lecturer, String currentPath) {
        List<NavigationItem> items = new ArrayList<>();

        int myMaterials = materialService.getMyMaterials(lecturer.getId()).size();
        long unreadNotifications = notificationService.countUnread(lecturer.getId());

        items.add(new NavigationItem(
            "My Dashboard", 
            "bi-speedometer2", 
            "/lecturer/dashboard", 
            unreadNotifications > 0 ? String.valueOf(unreadNotifications) : null,
            currentPath.contains("/lecturer/dashboard"),
            "Teaching overview"
        ));

        items.add(new NavigationItem(
            "My Materials", 
            "bi-journal-text", 
            "/lecturer/materials", 
            String.valueOf(myMaterials),
            currentPath.contains("/lecturer/materials"),
            "Manage teaching materials"
        ));

        items.add(new NavigationItem(
            "Classes", 
            "bi-people", 
            "/lecturer/classes", 
            null,
            currentPath.contains("/lecturer/classes"),
            "Student management"
        ));

        items.add(new NavigationItem(
            "Request Device", 
            "bi-laptop", 
            "/lecturer/request", 
            null,
            currentPath.contains("/lecturer/request"),
            "Request teaching equipment"
        ));

        items.add(new NavigationItem(
            "Learning", 
            "bi-book-fill", 
            "/learning", 
            null,
            currentPath.contains("/learning"),
            "Educational resources"
        ));

        return items;
    }

    /**
     * Staff-specific navigation focused on resource management
     */
    private List<NavigationItem> getStaffNavigation(User staff, String currentPath) {
        List<NavigationItem> items = new ArrayList<>();

        int pendingDevices = deviceService.getPendingApproval().size();
        int pendingMaterials = materialService.getPendingApproval().size();
        int myRequests = maintenanceService.getMyRequests(staff.getId()).size();
        long unreadNotifications = notificationService.countUnread(staff.getId());

        items.add(new NavigationItem(
            "Dashboard", 
            "bi-speedometer2", 
            "/staff/dashboard", 
            unreadNotifications > 0 ? String.valueOf(unreadNotifications) : null,
            currentPath.contains("/staff/dashboard"),
            "Staff operations overview"
        ));

        items.add(new NavigationItem(
            "Register Device", 
            "bi-laptop-fill", 
            "/staff/devices/add", 
            pendingDevices > 0 ? String.valueOf(pendingDevices) : null,
            currentPath.contains("/staff/devices"),
            "Register new devices"
        ));

        items.add(new NavigationItem(
            "Report Material", 
            "bi-box-fill", 
            "/staff/materials/add", 
            pendingMaterials > 0 ? String.valueOf(pendingMaterials) : null,
            currentPath.contains("/staff/materials"),
            "Report material issues"
        ));

        items.add(new NavigationItem(
            "Learning", 
            "bi-mortarboard-fill", 
            "/learning", 
            null,
            currentPath.contains("/learning"),
            "Training resources"
        ));

        return items;
    }

    /**
     * Cleaner-specific navigation focused on maintenance reporting
     */
    private List<NavigationItem> getCleanerNavigation(User cleaner, String currentPath) {
        List<NavigationItem> items = new ArrayList<>();

        int myReports = maintenanceService.getMyRequests(cleaner.getId()).size();
        long unreadNotifications = notificationService.countUnread(cleaner.getId());

        items.add(new NavigationItem(
            "Dashboard", 
            "bi-speedometer2", 
            "/cleaner/dashboard", 
            unreadNotifications > 0 ? String.valueOf(unreadNotifications) : null,
            currentPath.contains("/cleaner/dashboard"),
            "Maintenance overview"
        ));

        items.add(new NavigationItem(
            "New Report", 
            "bi-plus-circle-fill", 
            "/cleaner/report", 
            null,
            currentPath.contains("/cleaner/report"),
            "Report new issues"
        ));

        items.add(new NavigationItem(
            "Learning", 
            "bi-mortarboard-fill", 
            "/learning", 
            null,
            currentPath.contains("/learning"),
            "Training resources"
        ));

        return items;
    }

    /**
     * Get page title based on current path and user role
     */
    public String getPageTitle(String path, User user) {
        String rolePrefix = user.getRole().name().charAt(0) + 
                           user.getRole().name().substring(1).toLowerCase();
        
        if (path.contains("/dashboard")) return rolePrefix + " Dashboard";
        if (path.contains("/users")) return "User Management";
        if (path.contains("/devices")) return "Device Management";
        if (path.contains("/materials")) return "Material Management";
        if (path.contains("/maintenance")) return "Maintenance Operations";
        if (path.contains("/analytics")) return "System Analytics";
        if (path.contains("/tasks")) return "My Tasks";
        if (path.contains("/requests")) return "My Requests";
        if (path.contains("/learning")) return "Learning Centre";
        if (path.contains("/classes")) return "Class Management";
        
        return rolePrefix + " Portal";
    }

    /**
     * Get page subtitle/description for better context
     */
    public String getPageSubtitle(String path, User user) {
        if (path.contains("/dashboard")) return "System overview and quick actions";
        if (path.contains("/users")) return "Manage user accounts and permissions";
        if (path.contains("/devices")) return "Monitor and approve device registrations";
        if (path.contains("/materials")) return "Manage learning materials and resources";
        if (path.contains("/maintenance")) return "Assign and track maintenance tasks";
        if (path.contains("/analytics")) return "Performance metrics and insights";
        if (path.contains("/tasks")) return "Assigned maintenance tasks";
        if (path.contains("/requests")) return "Track your maintenance requests";
        if (path.contains("/learning")) return "Educational resources and training";
        if (path.contains("/classes")) return "Manage student classes and progress";
        
        return "Navigate your workspace";
    }
}
