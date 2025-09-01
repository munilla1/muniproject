
    // Tiempo de inactividad antes de redirigir (en milisegundos)
    const sessionTimeout = 30 * 60 * 1000; // 30 minutos

    // Configura un temporizador para redirigir al usuario
    setTimeout(() => {
        window.location.href = "/"; // Redirige a la página principal
    }, sessionTimeout);


	let contador1 = 0;

	function incrementar1() {
	    contador1++;
	    document.getElementById("contador1").textContent = contador1;
	}

	function decrementar1() {
	    if (contador1 > 0) { // Solo decrementar si el contador es mayor a 0
	        contador1--;
	        document.getElementById("contador1").textContent = contador1;
	    }
	}

	let contador2 = 0;

	function incrementar2() {
	    contador2++;
	    document.getElementById("contador2").textContent = contador2;
	}

	function decrementar2() {
	    if (contador2 > 0) { // Solo decrementar si el contador es mayor a 0
	        contador2--;
	        document.getElementById("contador2").textContent = contador2;
	    }
	}
	
	let contador3 = 0;

	function incrementar3() {
	    contador3++;
	    document.getElementById("contador3").textContent = contador3;
	}

	function decrementar3() {
	    if (contador3 > 0) { // Solo decrementar si el contador es mayor a 0
	        contador3--;
	        document.getElementById("contador3").textContent = contador3;
	    }
	}
	
	function aceptarValor1() {
	    let valorActual = document.getElementById("contador1").textContent;
	    alert(`Has creado: ${valorActual} unidades de infanteria`); // Muestra el valor actual en una alerta
	}
	
	function aceptarValor2() {
	    let valorActual = document.getElementById("contador2").textContent;
	    alert(`Has creado: ${valorActual} unidades de tanques`); // Muestra el valor actual en una alerta
	}

	function aceptarValor3() {
	    let valorActual = document.getElementById("contador3").textContent;
	    alert(`Has creado: ${valorActual} unidades de naves`); // Muestra el valor actual en una alerta
	}

	
	// Inicializamos el contador en 0
	let contador4 = 0;

	// Función para incrementar el contador cada segundo
	function iniciarContador1() {
	    setInterval(() => {
	        contador4++;
	        document.getElementById("contadorTiempo1").textContent = contador4;
	    }, 1000); // Incrementa cada 1000ms (1 segundo)
	}

	// Llamamos a la función para iniciar el contador
	iniciarContador1();

	let contador5 = 0;

		// Función para incrementar el contador cada segundo
		function iniciarContador2() {
		    setInterval(() => {
		        contador5++;
		        document.getElementById("contadorTiempo2").textContent = contador5;
		    }, 500); // Incrementa cada 500ms (1 segundo)
		}

		// Llamamos a la función para iniciar el contador
		iniciarContador2();
	
	
	
	
	
	
	