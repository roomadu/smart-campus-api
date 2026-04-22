const API = 'api/v1';

let data = { rooms: [], sensors: [], history: [] };

document.addEventListener('DOMContentLoaded', () => {
    lucide.createIcons();
    initNav();
    sync();
});

function initNav() {
    document.querySelectorAll('.nav-link').forEach(link => {
        link.onclick = (e) => {
            e.preventDefault();
            const view = link.dataset.view;
            document.querySelectorAll('.nav-link, .view-section').forEach(el => el.classList.remove('active'));
            link.classList.add('active');
            document.getElementById(view + '-view').classList.add('active');
            document.getElementById('view-title').innerText = link.innerText.trim();
        };
    });
}

async function sync() {
    try {
        const [meta, rms, sns] = await Promise.all([
            fetch(API).then(r => r.json()),
            fetch(API + '/rooms').then(r => r.json()),
            fetch(API + '/sensors').then(r => r.json())
        ]);

        document.getElementById('discovery-info').innerText = JSON.stringify(meta, null, 4);
        data.rooms = rms;
        data.sensors = sns;
        
        updateUI();
    } catch (e) { console.error("Sync Failed", e); }
}

function updateUI() {
    document.getElementById('count-rooms').innerText = data.rooms.length;
    document.getElementById('count-sensors').innerText = data.sensors.length;

    const roomsDiv = document.getElementById('rooms-list');
    roomsDiv.innerHTML = data.rooms.map(r => `
        <div style="background: var(--glass-bg); padding: 1.5rem; border-radius: 15px; margin-bottom: 1rem; border: 1px solid var(--glass-border)">
            <h4 style="color: var(--primary)">${r.name}</h4>
            <p style="font-size: 0.8rem; color: var(--text-muted)">${r.building} • Floor ${r.floor}</p>
        </div>
    `).join('');

    const sensorTbody = document.getElementById('sensors-table-body');
    sensorTbody.innerHTML = data.sensors.map(s => `
        <tr>
            <td><code>${s.sensorId}</code></td>
            <td>${s.type}</td>
            <td>${s.roomId}</td>
            <td><span class="badge badge-${s.status.toLowerCase()}">${s.status}</span></td>
            <td style="color: var(--primary); font-weight: 700">${s.currentValue} ${s.unit}</td>
            <td><button class="btn btn-secondary" style="padding: 4px 8px" onclick="loadReadings('${s.sensorId}')"><i data-lucide="activity"></i></button></td>
        </tr>
    `).join('');
    lucide.createIcons();
}

async function loadReadings(id) {
    const res = await fetch(`${API}/sensors/${id}/readings`);
    const history = await res.json();
    data.history = history;
    
    const tbody = document.getElementById('readings-table-body');
    tbody.innerHTML = history.map(h => `
        <tr>
            <td>${h.timestamp.split('.')[0]}</td>
            <td>${h.sensorId}</td>
            <td>${h.value}</td>
            <td>${h.unit}</td>
        </tr>
    `).join('');
    
    // Switch to telemetry view
    document.querySelector('[data-view="readings"]').click();
}

function refreshData() { sync(); }
