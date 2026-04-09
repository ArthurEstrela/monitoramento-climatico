let stompClient = null;

function connect() {
    const socket = new SockJS('/ws-clima');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Conectado: ' + frame);
        stompClient.subscribe('/topic/clima', function (message) {
            showWeather(JSON.parse(message.body));
        });
    }, function (error) {
        setConnected(false);
        console.error('Erro na conexão:', error);
        // Tenta reconectar em 5 segundos
        setTimeout(connect, 5000);
    });
}

function setConnected(connected) {
    const statusDiv = document.getElementById('status');
    if (connected) {
        statusDiv.innerText = 'Status: Conectado';
        statusDiv.className = 'connected';
    } else {
        statusDiv.innerText = 'Status: Desconectado';
        statusDiv.className = 'disconnected';
    }
}

function showWeather(data) {
    console.log('Dados recebidos:', data);
    const dashboard = document.getElementById('dashboard');
    let card = document.getElementById('card-' + data.cidade.replace(/\s+/g, '-'));

    if (!card) {
        card = document.createElement('div');
        card.id = 'card-' + data.cidade.replace(/\s+/g, '-');
        card.className = 'card';
        dashboard.appendChild(card);
    }

    // Determine color based on temperature
    let colorClass = 'temperado';
    if (data.temperatura < 20) {
        colorClass = 'frio';
    } else if (data.temperatura > 28) {
        colorClass = 'calor';
    }

    card.className = 'card ' + colorClass;
    
    card.innerHTML = `
        <div class="cidade">${data.cidade}</div>
        <div class="temp">${data.temperatura.toFixed(1)}°C</div>
        <div class="desc">${data.descricao}</div>
        <div class="horario">Atualizado em: ${data.horario}</div>
    `;
}

// Inicializar conexão
connect();
