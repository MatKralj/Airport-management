var wsocket;

function connect() {
    var aplikacija = "/" + document.location.pathname.split("/")[1];
    var wsUri = "ws://" + document.location.host + aplikacija +"/infoPutnik";
    wsocket = new WebSocket(wsUri);
    wsocket.onmessage = onMessage; 
}

function onMessage(evt)
{
    var poruka = JSON.parse(evt.data);
    var tekst = "";
    var odabraniPutnik = parseInt(document.getElementById("obrazac:odabraniPutnik").value);
    
    if (poruka.akcija==="brisanje" && poruka.putnik===odabraniPutnik) {
        tekst = "Obrisan let putniku: " + poruka.putnik;
        var preuzmiLetoveBtn = document.getElementById("obrazac:preuzmiLetovePr");
        preuzmiLetoveBtn.click();
    } else if (poruka.akcija==="upis" && poruka.putnik===odabraniPutnik) {
        tekst = "Dodan je let putniku: " + poruka.putnik;
        var preuzmiLetoveBtn = document.getElementById("obrazac:preuzmiLetovePr");
        preuzmiLetoveBtn.click();
    } else if(poruka.putnik===odabraniPutnik){
        tekst = "Nepoznata akcija nad letom za putnika: " + poruka.putnik;
    }
    if(tekst!==""){
        var porukaElement = document.getElementById("poruka");
        porukaElement.innerHTML = porukaElement.innerHTML + "<br/>" + tekst;
    }
    
}

window.addEventListener("load", connect, false);