/***********************************************
*                                              *
*                   SOCKET                     *
*                                              *
************************************************/

var webSocket = new WebSocket("ws://127.0.0.1:4444");

selection = 0;
received = null;

webSocket.onopen = function(message){ wsOpen(message); };
webSocket.onmessage = function(message){ wsGetMessage(message); };
webSocket.onclose = function(message){ wsClose(message); };
webSocket.onerror = function(message){ wsError(message); };

function wsOpen(message){}

function wsGetMessage(message){
	var robots_and_clusters_IR = JSON.parse(message.data);
	var a = robots_and_clusters_IR["9"];
	var b = a["91"];
	alert(b["cluster_ir"]);
}

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
