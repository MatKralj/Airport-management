var wsocket;

function connect() {
    var aplikacija = "/" + document.location.pathname.split("/")[1];
    var wsUri = "ws://" + document.location.host + aplikacija +"/infoJms";
    wsocket = new WebSocket(wsUri);
    wsocket.onmessage = onMessage; 
}

function onMessage(evt)
{
    var poruka = JSON.parse(evt.data);
    var tekst = "";
    
    if (poruka.vrsta==="JMS1") {
        tekst = "Dodana ili uklonjena poruka: JMS1";
        var preuzmiJms1Btn = document.getElementById("formaJms1:preuzmiJms1_btn");
        preuzmiJms1Btn.click();
    } else if (poruka.vrsta==="JMS2") {
        tekst = "Dodana ili uklonjena poruka: JMS2";
        var preuzmiJms2Btn = document.getElementById("formaJms2:preuzmiJms2_btn");
        preuzmiJms2Btn.click();
    }
    if(tekst!==""){
        var porukaElement = document.getElementById("poruka");
        porukaElement.innerHTML = porukaElement.innerHTML + "<br/>" + tekst;
    }
    
}

window.addEventListener("load", connect, false);