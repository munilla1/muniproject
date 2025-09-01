var stripe = Stripe("pk_test_51RMXBa4DXEGdL4YDZfpzre54KnxxTLrIkqui8KNg10suPsIJVbiTlo4Pn3g1rJ9FAm71F98fwbrT44oIjosz1h1l00oNSSPSAT");
    var elements = stripe.elements();
    var card = elements.create('card');
    card.mount('#card-element');

    var form = document.getElementById('payment-form');
    form.addEventListener('submit', function (event) {
        event.preventDefault();

        stripe.createToken(card).then(function (result) {
            if (result.error) {
                document.getElementById('card-errors').textContent = result.error.message;
            } else {
                var hiddenInput = document.createElement('input');
                hiddenInput.setAttribute('type', 'hidden');
                hiddenInput.setAttribute('name', 'stripeToken');
                hiddenInput.setAttribute('value', result.token.id);
                form.appendChild(hiddenInput);
                form.submit();
            }
        });
    });
