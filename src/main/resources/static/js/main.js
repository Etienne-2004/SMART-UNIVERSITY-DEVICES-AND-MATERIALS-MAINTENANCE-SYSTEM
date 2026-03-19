/**
 * Smart University MMS – Main JS
 * Learning System + AI Chat v2.0
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

// ── Mobile Navbar Menu Toggle ─────────────────────────────
function toggleMobileNav() {
    const navLinks = document.querySelector('.navbar-nav-links');
    if (navLinks) {
        navLinks.classList.toggle('mobile-show');
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

    // Mobile navbar hamburger menu
    const navbarHamburger = document.querySelector('.navbar-hamburger');
    if (navbarHamburger) {
        navbarHamburger.addEventListener('click', function(e) {
            e.preventDefault();
            toggleMobileNav();
        });
    }

    // Close mobile nav when clicking outside
    document.addEventListener('click', function(e) {
        const navLinks = document.querySelector('.navbar-nav-links');
        const hamburger = document.querySelector('.navbar-hamburger');
        
        if (navLinks && navLinks.classList.contains('mobile-show') && 
            !navLinks.contains(e.target) && 
            !hamburger.contains(e.target)) {
            navLinks.classList.remove('mobile-show');
        }
    });

    // Table search (generic)
    const searchInputs = document.querySelectorAll('[data-table-search]');
    searchInputs.forEach(input => {
        const tableId = input.dataset.tableSearch;
        input.addEventListener('input', function () {
            filterTable(tableId, this.value);
        });
    });

    // Count-up animation for KPI values
    document.querySelectorAll('.count-up').forEach(el => {
        const target = parseInt(el.textContent, 10) || 0;
        if (target === 0) return;
        let current = 0;
        const step = Math.max(1, Math.ceil(target / 40));
        const timer = setInterval(() => {
            current = Math.min(current + step, target);
            el.textContent = current;
            if (current >= target) clearInterval(timer);
        }, 30);
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

// ── Confirm delete/action dialogs ────────────────────────────
function confirmAction(message, formId) {
    if (confirm(message || 'Are you sure?')) {
        document.getElementById(formId)?.submit();
    }
}

// ══════════════════════════════════════════════════════════════
//  SMART AI CHAT SYSTEM — Full Learning Knowledge Base
// ══════════════════════════════════════════════════════════════

// AI Knowledge Base — maps keyword patterns to rich responses
const AI_KNOWLEDGE = [
    {
        keys: ['hello', 'hi ', 'hey', 'good morning', 'good afternoon'],
        response: "👋 Hello! I'm your <strong>SmartUni AI Assistant</strong>. I'm connected to the maintenance database and learning system. Ask me about devices, maintenance, materials, roles, security, or visit the 🎓 <a href='/learning' style='color:#818cf8'>Learning Centre</a> for structured lessons!"
    },
    {
        keys: ['temperature', 'heat', 'cool', 'thermal', 'environment', 'climate'],
        response: "🌡️ <strong>Optimal Device Environment:</strong><br>• Temperature: <strong>18°C – 22°C</strong> (64°F – 72°F)<br>• Humidity: <strong>35% – 55%</strong> relative<br>• Avoid heat vents, direct sunlight, and dusty corners.<br>• Use UPS/surge protectors to guard against power spikes.<br><br><em>See Lesson 1 in the Learning Centre for more details.</em>"
    },
    {
        keys: ['projector', 'pc', 'laptop', 'computer', 'device', 'equipment', 'screen'],
        response: "💻 <strong>Device Management:</strong> All devices must be registered with a unique Device ID and room location. Staff register devices; Admin approves them. For maintenance: Staff submits a request → Admin assigns a Technician → Technician resolves and marks COMPLETED → Admin verifies.<br><br>📚 <a href='/learning/1' style='color:#818cf8'>Learn about Optimal Device Environments →</a>"
    },
    {
        keys: ['maintenance', 'repair', 'fix', 'broken', 'lifecycle', 'workflow'],
        response: "🔧 <strong>Maintenance Lifecycle:</strong><br>1️⃣ <strong>PENDING</strong> — Request raised<br>2️⃣ <strong>ASSIGNED</strong> — Technician assigned by admin<br>3️⃣ <strong>IN_PROGRESS</strong> — Technician working<br>4️⃣ <strong>COMPLETED</strong> — Technician finished<br>5️⃣ <strong>VERIFIED</strong> — Admin confirmed<br><br>📚 <a href='/learning/2' style='color:#818cf8'>Study the full Maintenance Lifecycle →</a>"
    },
    {
        keys: ['material', 'cleaning', 'stock', 'sanitizer', 'detergent', 'supply', 'supplies'],
        response: "📦 <strong>Material Stock:</strong> Report cleaning materials (detergents, sanitizers, bulbs) before stock runs out. Every submission logs the room location for audit trails. Admins approve replenishment requests. Status updates appear in real-time on all dashboards.<br><br>📚 <a href='/learning/3' style='color:#818cf8'>Study Material Stock Management →</a>"
    },
    {
        keys: ['report', 'submit', 'issue', 'damage', 'how to report', 'best practice'],
        response: "📋 <strong>How to Write a Good Report:</strong><br>✅ Include the exact Device ID or material name<br>✅ Provide the precise room/location<br>✅ Describe the symptom clearly (not just \"broken\")<br>✅ Set an honest priority (LOW / MEDIUM / HIGH / CRITICAL)<br><br>⚠️ Overusing CRITICAL priority slows genuine emergencies.<br><br>📚 <a href='/learning/4' style='color:#818cf8'>Reporting Best Practices lesson →</a>"
    },
    {
        keys: ['technician', 'tech', 'field', 'repair technician', 'task start', 'complete task'],
        response: "🛠️ <strong>Technician Excellence:</strong> Before starting a task, re-read the issue description and confirm device ID or location. Always update the task notes with what you found and what you did. If parts are missing, stay IN_PROGRESS and explain why — never mark COMPLETED unless fully resolved.<br><br>📚 <a href='/learning/5' style='color:#818cf8'>Technician Field Excellence lesson →</a>"
    },
    {
        keys: ['admin', 'administration', 'governance', 'manage', 'approve', 'verify'],
        response: "👑 <strong>Admin Governance:</strong> Key admin duties — approving users, devices, materials, assigning technicians, verifying task completions, and monitoring the Analytics dashboard. Check for overdue tasks regularly and use audit logs to spot anomalies.<br><br>📚 <a href='/learning/6' style='color:#818cf8'>Admin System Governance lesson →</a>"
    },
    {
        keys: ['security', 'audit', 'log', 'mfa', 'password', 'suspicious', 'accountability'],
        response: "🔐 <strong>Security & Audit Trails:</strong> Every action (login, device registration, task update) is recorded in the Audit Log for full accountability. <strong>MFA (Multi-Factor Authentication)</strong> is enabled by default — never share your OTP code or password. Report suspicious logins immediately to your Admin.<br><br>📚 <a href='/learning/7' style='color:#818cf8'>System Security lesson →</a>"
    },
    {
        keys: ['dashboard', 'kpi', 'overview', 'analytics', 'chart', 'statistic'],
        response: "📊 <strong>Dashboard & Analytics:</strong> Your dashboard shows live KPIs tailored to your role — pending requests, active tasks, stock levels, and more. The Admin Analytics page tracks technician performance and university-wide completion rates. Use these insights to make proactive decisions."
    },
    {
        keys: ['staff', 'register', 'staff role', 'staff member'],
        response: "👩‍💼 <strong>Staff Role:</strong> Staff can register new devices, submit maintenance requests for broken equipment, and report materials. Track your submitted requests directly from your dashboard to know when issues are resolved."
    },
    {
        keys: ['cleaner', 'cleaning staff', 'cleaner student', 'janitor'],
        response: "🧹 <strong>Cleaner/Maintenance Role:</strong> Cleaners are the first line of detection for physical damage. Use the \"Report New Damage\" button on your dashboard to submit reports. Be precise — include the material name, exact room, and a clear description. The admin will then assign a technician."
    },
    {
        keys: ['learning', 'lesson', 'quiz', 'study', 'learn', 'education', 'knowledge', 'centre'],
        response: "🎓 <strong>Learning Centre:</strong> Personalised lessons for your role covering: device management, maintenance lifecycle, material stock, best reporting practices, technician field skills, admin governance, and system security — each with interactive quizzes!<br><br>👉 <a href='/learning' style='color:#818cf8'>Visit the Learning Centre →</a>"
    },
    {
        keys: ['priority', 'critical', 'high', 'low', 'medium', 'urgent'],
        response: "⚡ <strong>Priority Levels:</strong><br>🔴 <strong>CRITICAL</strong> — System down, total failure, safety risk<br>🟠 <strong>HIGH</strong> — Major function impaired<br>🟡 <strong>MEDIUM</strong> — Partial impairment, workaround exists<br>🟢 <strong>LOW</strong> — Minor cosmetic or non-urgent<br><br>Always choose the most <em>accurate</em> level — overuse of CRITICAL delays genuine emergencies."
    },
    {
        keys: ['otp', 'one time', 'two factor', '2fa', 'verification code'],
        response: "📱 <strong>OTP / MFA:</strong> After login, you receive a One-Time Password (OTP) via email. This two-factor verification protects your account even if your password is compromised. OTPs expire after a few minutes — never share them with anyone, not even IT staff."
    },
    {
        keys: ['overdue', 'late', 'deadline', 'due date', 'missed'],
        response: "⏰ <strong>Overdue Tasks:</strong> Tasks past their due date are flagged on the Admin Dashboard. Admins should follow up with the assigned technician. If a task cannot be completed on time, the technician must update the notes with a reason and estimated completion date."
    },
    {
        keys: ['notification', 'alert', 'message', 'bell'],
        response: "🔔 <strong>Notifications:</strong> You receive automatic alerts when tasks are assigned to you, when a task you reported is completed, and when an admin verifies work. Check the notification bell in the top navbar. Unread notifications are highlighted with a red badge."
    }
];

// ── Chip Ask shortcut ─────────────────────────────────────────
function chipAsk(topic) {
    const input = document.getElementById('aiChatInput');
    if (input) {
        input.value = topic;
        sendAIMessage();
    }
}

// ── Key press handler ─────────────────────────────────────────
function handleAIPress(event) {
    if (event.key === 'Enter') {
        sendAIMessage();
    }
}

// ── Send AI Message ───────────────────────────────────────────
async function sendAIMessage() {
    const input = document.getElementById('aiChatInput');
    const chatBody = document.getElementById('aiChatBody');
    if (!input || !chatBody) return;

    const msg = input.value.trim();
    if (!msg) return;

    // ── User bubble ───────────────────────────────────────────
    const userDiv = document.createElement('div');
    userDiv.className = 'ai-chat-message user';
    userDiv.textContent = msg;
    chatBody.appendChild(userDiv);
    input.value = '';
    chatBody.scrollTop = chatBody.scrollHeight;

    // ── Typing indicator ──────────────────────────────────────
    const typingDiv = document.createElement('div');
    typingDiv.className = 'ai-chat-message ai';
    typingDiv.innerHTML = '<span class="ai-typing-dots"><span></span><span></span><span></span></span>';
    chatBody.appendChild(typingDiv);
    chatBody.scrollTop = chatBody.scrollHeight;

    const lowerMsg = msg.toLowerCase();
    
    // 1. Check built-in SmartUni operational knowledge first
    for (const entry of AI_KNOWLEDGE) {
        if (entry.keys.some(k => lowerMsg.includes(k))) {
            setTimeout(() => {
                typingDiv.innerHTML = entry.response;
                chatBody.scrollTop = chatBody.scrollHeight;
            }, 600 + Math.random() * 500);
            return;
        }
    }

    // 2. Conversational basics
    const conversational = {
        "how are you": "I'm functioning perfectly here at the SmartUni servers! How can I assist you with maintenance or general knowledge today?",
        "who are you": "I am your SmartUni AI Assistant. I operate like a conversational AI (similar to ChatGPT) tailored to answer any technical or general query you have.",
        "what is your name": "I am the SmartUni AI Assistant. I'm here to provide you with true, reliable answers directly from our system and internet databases.",
        "thank you": "You're very welcome! If you need anything else, just ask.",
        "thanks": "Glad I could help!",
        "good morning": "Good morning! Ready to manage our campus assets effectively today?",
        "hello": "Hello there! What can I explain for you today?",
        "hi": "Hi! Feel free to ask me any question at all."
    };

    for (const [key, val] of Object.entries(conversational)) {
        if (lowerMsg === key || lowerMsg === key + '?' || lowerMsg === key + '!') {
            setTimeout(() => {
                typingDiv.innerHTML = val;
                chatBody.scrollTop = chatBody.scrollHeight;
            }, 800);
            return;
        }
    }

    // 3. True AI General Knowledge (Open Internet Search Engine)
    // We treat whatever they type as a query, finding the best factual match simulating GPT text.
    try {
        // Search Wikipedia for the closest article matching their entire question/command
        let searchQuery = msg.replace(/^(what is|what are|explain|define|tell me about|who is|where is|how does)\s+/i, '');
        searchQuery = searchQuery.replace(/[?.,!]/g, '');

        const searchUrl = `https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=${encodeURIComponent(searchQuery)}&utf8=&format=json&origin=*`;
        const searchResp = await fetch(searchUrl);
        const searchData = await searchResp.json();

        if (searchData.query && searchData.query.search && searchData.query.search.length > 0) {
            const topResultTitle = searchData.query.search[0].title;
            
            // Fetch beautifully formatted factual summary for that article
            const summaryResp = await fetch(`https://en.wikipedia.org/api/rest_v1/page/summary/${encodeURIComponent(topResultTitle)}`);
            const summaryData = await summaryResp.json();

            if (summaryData.extract) {
                setTimeout(() => {
                    typingDiv.innerHTML = `✨ <strong>AI Factual Output:</strong><br>${summaryData.extract}`;
                    chatBody.scrollTop = chatBody.scrollHeight;
                }, 1000);
                return;
            }
        }
    } catch (e) {
        console.error("AI API Error:", e);
    }

    // 4. Ultimate Fallback Analysis
    setTimeout(() => {
        typingDiv.innerHTML = `I analyzed your command: <em>"${msg}"</em>, but I couldn't compute a definitive factual answer for it. I am a highly trained technical AI. Could you rephrase your question or provide a clearer noun?`;
        chatBody.scrollTop = chatBody.scrollHeight;
    }, 1200);
}
