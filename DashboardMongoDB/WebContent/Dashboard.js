/***********************************************
*                                              *
*                   SOCKET                     *
*                                              *
************************************************/
var webSocket = new WebSocket("ws://localhost:8080/DashboardMySQL/test");

selection = 0;
received = null;

webSocket.onopen = function(message){ wsOpen(message); };
webSocket.onmessage = function(message){ wsGetMessage(message); };
webSocket.onclose = function(message){ wsClose(message); };
webSocket.onerror = function(message){ wsError(message); };

function wsOpen(message){}

function wsGetMessage(message){}

function wsClose(message){
  alert("Connection closed");
}

function wserror(message){}

//function wsSendMessage(){}
//function wsCloseConnection(){}

/***********************************************
*                                              *
*                 END SOCKET                   *
*                                              *
************************************************/

// Function to change background color
// according to robots and Clusters IR.
$(document).ready(function(){
  $('.object').each(function(i, obj) {
    if( $(this).find(".object-ir").text().split('%')[0] >= 40 )
      $(this).css({"background-color" : "red"});
    else
      $(this).css({"background-color" : "green"});
  });
});
