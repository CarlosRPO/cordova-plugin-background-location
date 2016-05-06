Plugin personalizado de Cordova para obtener geolocalización en Background y sincronizar a través de un Servicio Web todas las posiciones obtenidas.

# Platformas:

    - Android

# Instalación: 

    cordova plugin add [URL]

# Iniciar:

    window.backLocation.start(
        // Success Callback
        function(response) {
          console.log('Response: ' + response);
        }, 
        // Error Callback
        function(error) {
          console.log('Error: ' + error);
        },
        // Params
        [
            {
            url: ['END_POINT'] // URL Web Service
            }
        ]
    );

