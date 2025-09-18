// Dashboard JavaScript Functionality

// Chart instances
let tempHumidityChart;
let lightChart;

// Initialize charts when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    initializeCharts();
    startRealTimeUpdates();
});

// Initialize Temperature & Humidity Chart
function initializeCharts() {
    const tempHumidityCtx = document.getElementById('tempHumidityChart');
    if (tempHumidityCtx) {
        const ctx = tempHumidityCtx.getContext('2d');
        tempHumidityChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: ['18:04:00', '18:04:05', '18:04:10', '18:04:15', '18:04:20'],
                datasets: [{
                    label: 'Temperature (°C)',
                    data: [29, 30, 29, 31, 29],
                    borderColor: '#ff6b6b',
                    backgroundColor: 'rgba(255, 107, 107, 0.1)',
                    tension: 0.4,
                    pointRadius: 6,
                    pointBackgroundColor: '#ff6b6b'
                }, {
                    label: 'Humidity (%)',
                    data: [70, 72, 75, 73, 70],
                    borderColor: '#4ecdc4',
                    backgroundColor: 'rgba(78, 205, 196, 0.1)',
                    tension: 0.4,
                    pointRadius: 6,
                    pointBackgroundColor: '#4ecdc4'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 100,
                        grid: {
                            color: 'rgba(0,0,0,0.1)'
                        }
                    },
                    x: {
                        grid: {
                            color: 'rgba(0,0,0,0.1)'
                        }
                    }
                },
                plugins: {
                    legend: {
                        position: 'top'
                    }
                }
            }
        });
    }

    // Initialize Light Chart
    const lightCtx = document.getElementById('lightChart');
    if (lightCtx) {
        const ctx = lightCtx.getContext('2d');
        lightChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: ['18:04:06', '18:04:08', '18:04:10', '18:04:12', '18:04:14', '18:04:16', '18:04:18', '18:04:20'],
                datasets: [{
                    label: 'Light (Lux)',
                    data: [700, 720, 750, 730, 740, 760, 750, 800],
                    borderColor: '#f39c12',
                    backgroundColor: 'rgba(243, 156, 18, 0.1)',
                    tension: 0.4,
                    pointRadius: 6,
                    pointBackgroundColor: '#f39c12'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 1000,
                        grid: {
                            color: 'rgba(0,0,0,0.1)'
                        }
                    },
                    x: {
                        grid: {
                            color: 'rgba(0,0,0,0.1)'
                        }
                    }
                },
                plugins: {
                    legend: {
                        position: 'top'
                    }
                }
            }
        });
    }
}

// Device Toggle Function
function toggleDevice(element, deviceType) {
    const statusIndicator = element.parentElement.querySelector('.status-indicator');
    const isActive = element.classList.contains('active');
    
    if (isActive) {
        element.classList.remove('active');
        statusIndicator.textContent = 'Off';
        statusIndicator.className = 'status-indicator status-off';
    } else {
        element.classList.add('active');
        statusIndicator.textContent = 'On';
        statusIndicator.className = 'status-indicator status-on';
    }
    
    // Here you can add API calls to control actual devices
    console.log(`${deviceType} turned ${isActive ? 'off' : 'on'}`);
    
    // Example API call (uncomment when backend is ready)
    // sendDeviceCommand(deviceType, !isActive);
}

// Send device command to backend (for future use)
function sendDeviceCommand(deviceType, isOn) {
    fetch('/api/device/control', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            deviceType: deviceType,
            status: isOn ? 'ON' : 'OFF'
        })
    })
    .then(response => response.json())
    .then(data => {
        console.log('Device command sent:', data);
    })
    .catch(error => {
        console.error('Error sending device command:', error);
    });
}

// Real-time data updates
function startRealTimeUpdates() {
    setInterval(() => {
        updateChartData();
    }, 5000); // Update every 5 seconds
}

// Update chart data with new values
function updateChartData() {
    if (tempHumidityChart) {
        // Update temperature data
        const tempData = tempHumidityChart.data.datasets[0].data;
        tempData.shift();
        tempData.push(Math.floor(Math.random() * 5) + 28);
        
        // Update humidity data
        const humData = tempHumidityChart.data.datasets[1].data;
        humData.shift();
        humData.push(Math.floor(Math.random() * 10) + 65);
        
        // Update time labels
        const now = new Date();
        const timeString = now.toTimeString().slice(0, 8);
        
        tempHumidityChart.data.labels.shift();
        tempHumidityChart.data.labels.push(timeString);
        
        tempHumidityChart.update('none');
    }
    
    if (lightChart) {
        // Update light data
        const lightData = lightChart.data.datasets[0].data;
        lightData.shift();
        lightData.push(Math.floor(Math.random() * 100) + 700);
        
        // Update time labels
        const now = new Date();
        const timeString = now.toTimeString().slice(0, 8);
        
        lightChart.data.labels.shift();
        lightChart.data.labels.push(timeString);
        
        lightChart.update('none');
    }
}

// Update sensor card values (for future real-time updates)
function updateSensorValues(temperature, humidity, light) {
    const tempElement = document.querySelector('.sensor-card.temperature .sensor-value');
    const humElement = document.querySelector('.sensor-card.humidity .sensor-value');
    const lightElement = document.querySelector('.sensor-card.light .sensor-value');
    
    if (tempElement) tempElement.textContent = `${temperature}°C`;
    if (humElement) humElement.textContent = `${humidity}%`;
    if (lightElement) lightElement.textContent = `${light} Lux`;
}

// Fetch real sensor data from backend (for future use)
function fetchSensorData() {
    fetch('/dashboard/api/sensor-data')
        .then(response => response.json())
        .then(data => {
            if (data && data.length > 0) {
                const latestData = data[0]; // Get the most recent data
                updateSensorValues(latestData.temperature, latestData.humidity, latestData.lightLevel);
                
                // Update charts with real data if available
                updateChartsWithRealData(data);
            }
        })
        .catch(error => {
            console.error('Error fetching sensor data:', error);
        });
}

// Update charts with real sensor data
function updateChartsWithRealData(sensorData) {
    if (tempHumidityChart && sensorData.length > 0) {
        // Update temperature and humidity charts with real data
        const tempData = sensorData.map(item => item.temperature).slice(0, 10);
        const humData = sensorData.map(item => item.humidity).slice(0, 10);
        const timeLabels = sensorData.map(item => {
            const date = new Date(item.timestamp);
            return date.toLocaleTimeString('en-US', { 
                hour12: false, 
                hour: '2-digit', 
                minute: '2-digit', 
                second: '2-digit' 
            });
        }).slice(0, 10);
        
        tempHumidityChart.data.labels = timeLabels;
        tempHumidityChart.data.datasets[0].data = tempData;
        tempHumidityChart.data.datasets[1].data = humData;
        tempHumidityChart.update();
    }
    
    if (lightChart && sensorData.length > 0) {
        // Update light chart with real data
        const lightData = sensorData.map(item => item.lightLevel).slice(0, 10);
        const timeLabels = sensorData.map(item => {
            const date = new Date(item.timestamp);
            return date.toLocaleTimeString('en-US', { 
                hour12: false, 
                hour: '2-digit', 
                minute: '2-digit', 
                second: '2-digit' 
            });
        }).slice(0, 10);
        
        lightChart.data.labels = timeLabels;
        lightChart.data.datasets[0].data = lightData;
        lightChart.update();
    }
}

// Export functions for global use
window.toggleDevice = toggleDevice;
window.fetchSensorData = fetchSensorData;
