# Phase 8: Advanced Features & Business Rules Implementation

This document describes the business rules, validation logic, filtering capabilities, and workflows implemented in Phase 8 of the Smart University Devices and Materials Maintenance System.

## 1. Workflows & State Management

### Maintenance Task Workflow
The system implements a rigorous state machine for maintenance tasks, ensuring accountability and proper tracking of all reported issues.

*   **PENDING**: A user reports a `Device` or `Material` issue. The system validates the input, creates a maintenance request, and automatically notifies all administrators in the user's university.
*   **ASSIGNED**: An administrator assigns a technician to the pending request, specifying a due date. The system validates that the technician exists and sends a dedicated notification to them (`NotificationType.TASK_ASSIGNED`).
*   **IN_PROGRESS**: The assigned technician clicks "Start Task". The system enforces security validation to ensure that **only the assigned technician** can transition the task to this state.
*   **COMPLETED**: The technician finishes the job and submits optional notes and photo evidence. The system transitions the task state, logs the completion time, and sends a notification back to the admins (`NotificationType.TASK_COMPLETED`).
*   **VERIFIED**: An administrator reviews the completed task. The system updates the request state and, if it is a material issue, synchronizes the material's status to `VERIFIED`.

## 2. Advanced Backend Logic

### Notification System Integration
The platform incorporates a robust, real-time notification engine linked to core domain activities.
- Automatically pushes `SYSTEM`, `TASK_ASSIGNED`, `TASK_COMPLETED`, and `TASK_VERIFIED` alerts based on business events.
- Broadcasts messages globally for administrators or specifically targeted individuals (like a technician).

### OTP Authentication & Email Verification
Registration flows are constrained by stringent business rules:
- Users cannot access the system until their email is verified via OTP.
- The `OtpService` enforces time-based validations to expire OTPs after a predefined timeframe.
- Rate-limiting or request filtering guarantees that repeated spam of OTPs or false inputs are rejected.

## 3. Filtering & Analytics Logic

### Smart Filtering
- The system prevents cross-contamination of data: filtering queries enforce that the returned lists of Maintenance Requests belong *only* to the specific `University` or `College`.
- Technicians view distinct filtered dashboards: `Assigned Tasks`, `In Progress Tasks`, and `Completed Tasks`.

### Performance Analytics
- Implemented `getTechnicianPerformance` logic to compute performance metrics for technicians.
- Enables computing overdue tasks actively via `findOverdueTasks(LocalDateTime.now())`, providing administrators real-time risk assessment parameters.

## 4. Input Validation & Security

- Cross-validation explicitly occurs before state transitions (e.g., trying to start an application task not assigned to you invokes a `SecurityException`).
- All critical CRUD endpoints perform strict Not-Null validation and reference validation (checking if entities like `technicianId` or `requestId` genuinely exist before allocation).
