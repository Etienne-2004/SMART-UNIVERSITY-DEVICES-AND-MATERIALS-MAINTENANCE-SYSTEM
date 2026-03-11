/**
 * Smart University MMS – Main JS
 */

// ── Sidebar Toggle ────────────────────────────────────────────
function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    const icon = document.getElementById('sidebarToggleIcon');
    if (sidebar) {
        sidebar.classList.toggle('collapsed');
        if (icon) {
            icon.className = sidebar.classList.contains('collapsed')
                ? 'bi bi-chevron-right'
                : 'bi bi-chevron-left';
        }
    }
}

// ── Auto-dismiss alerts ────────────────────────────────────── 
document.addEventListener('DOMContentLoaded', function () {
    // Auto-dismiss flash alerts after 4 seconds
    const alerts = document.querySelectorAll('.alert.alert-dismissible');
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            if (bsAlert) bsAlert.close();
        }, 4000);
    });

    // Active tooltips
    const tooltips = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    tooltips.forEach(el => new bootstrap.Tooltip(el));

    // Mark notification as read on click
    document.querySelectorAll('.notif-item').forEach(item => {
        item.addEventListener('click', function () {
            this.classList.remove('unread');
        });
    });

    // Table search (generic)
    const searchInputs = document.querySelectorAll('[data-table-search]');
    searchInputs.forEach(input => {
        const tableId = input.dataset.tableSearch;
        input.addEventListener('input', function () {
            filterTable(tableId, this.value);
        });
    });
});

// ── Generic table filter ──────────────────────────────────────
function filterTable(tableId, query) {
    query = query.toLowerCase();
    const table = document.getElementById(tableId);
    if (!table) return;
    table.querySelectorAll('tbody tr').forEach(row => {
        row.style.display = row.textContent.toLowerCase().includes(query) ? '' : 'none';
    });
}

// ── Password strength indicator ───────────────────────────────
function checkPasswordStrength(password) {
    let strength = 0;
    if (password.length >= 8) strength++;
    if (/[A-Z]/.test(password)) strength++;
    if (/[0-9]/.test(password)) strength++;
    if (/[^A-Za-z0-9]/.test(password)) strength++;
    return strength;
}

// ── Confirm delete/action dialogs ────────────────────────────
function confirmAction(message, formId) {
    if (confirm(message || 'Are you sure?')) {
        document.getElementById(formId)?.submit();
    }
}

// ── AI Chat Simulator ─────────────────────────────────────────
function handleAIPress(event) {
    if (event.key === 'Enter') {
        sendAIMessage();
    }
}

function sendAIMessage() {
    const input = document.getElementById('aiChatInput');
    const msg = input.value.trim();
    if (!msg) return;

    const chatBody = document.getElementById('aiChatBody');

    // Add User Message
    const userDiv = document.createElement('div');
    userDiv.className = 'ai-chat-message user';
    userDiv.textContent = msg;
    chatBody.appendChild(userDiv);

    // Clear and Scroll
    input.value = '';
    chatBody.scrollTop = chatBody.scrollHeight;

    // Simulate AI Response Loading
    setTimeout(() => {
        const aiDiv = document.createElement('div');
        aiDiv.className = 'ai-chat-message ai';

        const lowerMsg = msg.toLowerCase();
        let response = "I'm still learning and cannot process that specific request yet. But I am always being updated! As your SmartUni Assistant, ask me about reliable device conditions like projectors or PCs!";

        if (lowerMsg.includes('temperature') || lowerMsg.includes('projector') || lowerMsg.includes('pc') || lowerMsg.includes('condition')) {
            response = "For optimal longevity of electronic devices like PCs and Projectors, the most reliable room temperature is between 18°C and 22°C (64°F - 72°F). Also keep the relative humidity between 35% and 55% to prevent internal moisture condensation and static buildup.";
        }

        aiDiv.innerHTML = response;
        chatBody.appendChild(aiDiv);
        chatBody.scrollTop = chatBody.scrollHeight;
    }, 1200);
}
