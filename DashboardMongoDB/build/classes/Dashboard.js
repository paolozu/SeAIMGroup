/***********************************************
*                                              *
*                   SOCKET                     *
*                                              *
************************************************/

var webSocket = new WebSocket("ws://192.168.159.111:4444");

selection = "areas";
selection_index;
received;

webSocket.onopen = function(message){ wsOpen(message); };
webSocket.onmessage = function(message){ wsGetMessage(message); };
webSocket.onclose = function(message){ wsClose(message); };
webSocket.onerror = function(message){ wsError(message); };

function wsOpen(message){}

function wsGetMessage(message){
	var robots_and_clusters_IR = JSON.parse(message.data);
	if( selection == "areas" ){
		var areas_count = Object.keys(robots_and_clusters_IR).length;
		showAreasView(areas_count);
	}	
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
function showAreasView(areas_count){
	$(document).ready(function(){
		$('#section').html("Aree");
		$('#section-number').html("");
		var a = $('#ir-viewer-container');
		for( i = 0; i < areas_count; i++ ){
			a.append("<div class=\"area\"><span class=\"area-id\">Area" + i + "</span></div>");
		}
	});
}

function color(){
	$(document).ready(function(){
	  $('.object').each(function(i, obj) {
	    if( $(this).find(".object-ir").text().split('%')[0] >= 40 )
	      $(this).css({"background-color" : "red"});
	    else
	      $(this).css({"background-color" : "green"});
	  });
	});
}
