# 🎓 Smart University Maintenance System

**Intelligent University Devices & Materials Maintenance Platform**  
*National University Maintenance System · Rwanda*

---

## 📋 Overview

The Smart University Maintenance System is a comprehensive, role-based platform designed to streamline the management of university devices, materials, and maintenance operations. Built with Spring Boot MVC and featuring intelligent navigation, real-time data synchronization, and responsive design.

## 🚀 Key Features

### 🎯 **Smart Navigation System (Phase 5-7 Complete)**
- **Role-Based Dynamic Navigation**: Contextual navigation for Admin, Technician, Staff, and Cleaner roles
- **Perfect Cross-Dashboard Consistency**: Sidebar ↔ Navbar ↔ Quick Actions = 100% Matching
- **Real-Time Badge Synchronization**: Live counts for pending tasks, approvals, and notifications
- **Mobile-Responsive Design**: Adaptive navigation with hamburger menu for all screen sizes

### 🎨 **Enhanced User Experience**
- **Campus-Focused Interface**: University-specific terminology and context
- **Gamification Elements**: Impact scores, achievement badges, and progress tracking
- **Professional Admin-Style Layouts**: Consistent design language across all dashboards
- **Intelligent AI Assistant**: Smart chat support for maintenance queries

### 📊 **Role-Specific Dashboards**

#### 🎯 **Administrator Dashboard**
- **Full System Control**: Users, Devices, Materials, Maintenance, Analytics
- **Real-Time Monitoring**: Pending approvals, system metrics, performance analytics
- **Advanced Analytics**: Comprehensive reporting and insights
- **Learning Centre**: Training resources and documentation

#### 🔧 **Technician Dashboard**
- **Admin-Style Structure**: Professional interface with technician-specific URLs
- **Task Management**: Assigned tasks, completion tracking, performance metrics
- **Resource Access**: View users, devices, materials, maintenance operations
- **Technical Analytics**: Performance insights and workload distribution

#### 👨‍🏫 **Staff Dashboard**
- **Resource Management**: Device registration, material reporting
- **Request Tracking**: Monitor submissions and approvals
- **Campus Operations**: Focused tools for daily staff activities
- **Learning Resources**: Professional development materials

#### 🧹 **Cleaner Dashboard**
- **Maintenance Intelligence**: Campus-focused reporting system
- **Impact Tracking**: Environmental impact and contribution metrics
- **Smart Reporting**: Guided issue reporting with photo uploads
- **Learning Centre**: Training modules and best practices

## 🛠️ Technical Architecture

### **Backend Technologies**
- **Spring Boot MVC**: Robust Java framework for enterprise applications
- **Spring Security**: Comprehensive authentication and authorization
- **Thymeleaf Templates**: Server-side templating with dynamic content
- **JPA/Hibernate**: Database ORM with MySQL integration
- **Role-Based Access Control**: Secure, granular permissions

### **Frontend Technologies**
- **Bootstrap 5**: Responsive CSS framework
- **Bootstrap Icons**: Comprehensive icon library
- **Custom CSS**: Professional university-themed styling
- **JavaScript ES6**: Modern client-side interactivity
- **Responsive Design**: Mobile-first approach

## 📱 Navigation Structure

### **Dynamic Navigation Items**

#### 🎯 **Admin Navigation**
```
Dashboard → /admin/dashboard
Users → /admin/users
Devices → /admin/devices
Materials → /admin/materials
Maintenance → /admin/maintenance
Analytics → /admin/analytics
Learning → /learning
```

#### 🔧 **Technician Navigation**
```
Dashboard → /technician/dashboard
Users → /technician/users
Devices → /technician/devices
Materials → /technician/materials
Maintenance → /technician/maintenance
Analytics → /technician/analytics
Learning → /learning
```

#### 👨‍🏫 **Staff Navigation**
```
Dashboard → /staff/dashboard
Register Device → /staff/devices/add
Report Material → /staff/materials/add
Learning → /learning
```

#### 🧹 **Cleaner Navigation**
```
Dashboard → /cleaner/dashboard
New Report → /cleaner/report
Learning → /learning
```

## 🔧 Installation & Setup

### **Prerequisites**
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Node.js 16+ (for frontend tools)

### **Quick Start**
```bash
# Clone the repository
git clone https://github.com/Etienne-2004/SMART-UNIVERSITY-DEVICES-AND-MATERIALS-MAINTENANCE-SYSTEM.git

# Navigate to project directory
cd SMART-UNIVERSITY-DEVICES-AND-MATERIALS-MAINTENANCE-SYSTEM

# Run the application
./RUN_SYSTEM.bat
```

## 🎓 Phase Development

### **Phase 5: Smart Navigation Infrastructure**
- ✅ NavigationService implementation
- ✅ Real-time badge system
- ✅ Responsive navbar design
- ✅ Mobile-friendly navigation

### **Phase 6: Role-Specific Dashboard Enhancements**
- ✅ Admin dashboard analytics
- ✅ Technician admin-style interface
- ✅ Staff resource management
- ✅ Cleaner campus intelligence

### **Phase 7: Perfect Navigation Matching**
- ✅ Sidebar ↔ Navbar ↔ Quick Actions consistency
- ✅ Cross-dashboard synchronization
- ✅ Role-appropriate terminology
- ✅ Professional user experience

### **Phase 7: Authentication & Security**
- ✅ **Login System**: Secure user authentication with OTP verification
- ✅ **Role-Based Access**: ADMIN, TECHNICIAN, STAFF, CLEANER_STUDENT roles
- ✅ **JWT/Security Configuration**: Spring Security with comprehensive security model
- ✅ **Security Model Explanation**: Detailed security architecture documentation
- ✅ **Auth Endpoints**: Complete authentication endpoints available on GitHub
- ✅ **Security Phase Submission**: Phase 7 security implementation completed (14 Mar – 20 Mar 2026)

#### **🔐 Security Features:**
- **Two-Factor Authentication**: OTP-based login system
- **Role-Based Authorization**: Granular access control by user role
- **Session Management**: Secure session handling and timeout
- **Password Security**: Encrypted password storage with BCrypt
- **CSRF Protection**: Cross-site request forgery prevention
- **Input Validation**: Comprehensive input sanitization

#### **🛡️ Security Architecture:**
- **Spring Security Framework**: Enterprise-grade security implementation
- **JWT Token Management**: Secure token-based authentication
- **Role Hierarchy**: Proper role-based access control
- **Security Filters**: Custom authentication and authorization filters
- **Secure Endpoints**: Protected API endpoints with role validation

#### **📊 Authentication Flow:**
1. **Login Request**: User submits credentials
2. **OTP Generation**: System generates one-time password
3. **OTP Verification**: User verifies identity with OTP
4. **JWT Token**: Secure token issued for session
5. **Role-Based Access**: User access based on assigned role
6. **Session Management**: Secure session handling throughout

## 📞 Support & Contact

### **Technical Support**
- **Email**: harindintwarietiennee@gmail.com
- **WhatsApp**: +250 793 719 113
- **GitHub Issues**: [Report Issues](https://github.com/Etienne-2004/SMART-UNIVERSITY-DEVICES-AND-MATERIALS-MAINTENANCE-SYSTEM/issues)

---

## 🎓 Conclusion

The Smart University Maintenance System represents a comprehensive solution for university device and material management. With intelligent role-based navigation, real-time features, and a professional user interface, this system provides an efficient, secure, and user-friendly platform for all university stakeholders.

**Built with ❤️ for Rwanda National University Maintenance Operations**

---

*© 2025 Rwanda Education Board · National Maintenance Platform*