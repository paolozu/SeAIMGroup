/***********************************************
*                                              *
*                   SOCKET                     *
*                                              *
************************************************/

var webSocket;

var selected_area = null;
var selected_cluster = null;
var robots_and_clusters_IR = null;

$.getJSON('https://api.ipify.org?format=json', function(data){
		var IP_ADDRESS;
		if( data.ip == "195.32.88.220" )   // PUT PUBLIC IP HERE.
			IP_ADDRESS = "192.168.1.32";     // PUT YOUR LOCAL IP HERE.
		else
			IP_ADDRESS = "195.32.88.220";    // PUT PUBLIC IP HERE.

		webSocket = new WebSocket("ws://" + IP_ADDRESS + ":4444");

		webSocket.onopen = function(message){ wsOpen(message); };
		webSocket.onmessage = function(message){ wsGetMessage(message); };
		webSocket.onclose = function(message){ wsClose(message); };
		webSocket.onerror = function(message){ wsError(message); };
});

function wsOpen(message){}

function wsGetMessage(message){
	robots_and_clusters_IR = JSON.parse(message.data);
	if( selected_area == null )
		showAreasView();
	if( selected_area != null && selected_cluster == null )
		showClustersView();
	if( selected_area != null && selected_cluster != null )
		showRobotsView();
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

$(document).on('click', ".area", function(){
	selected_area = $(this).find('span').html().replace('Area ', '');
	showClustersView();
});

$(document).on('click', ".cluster", function(){
	selected_cluster = $(this).find('span').html().replace('Cluster ', '');
	showRobotsView();
});

$(document).on('click', "#change-selection", function(){
	if($(this).html() == "Torna ai Clusters"){
		selected_cluster = null;
		showClustersView();
	}
	else if($(this).html() == "Torna alle Aree"){
		selected_area = null;
		showAreasView();
	}
});

function color(){
	$(document).ready(function(){
	  $('.robot, .cluster').each(function(i, obj) {
	    if( $(this).find(".ir").text().split('%')[0] >= 40 )
	      $(this).css({"background-color" : "red"});
	    else
	      $(this).css({"background-color" : "green"});
	  });
	});
}

function showAreasView(){
	$(document).ready(function(){
		$("#change-selection").hide();
		$('#loader').hide();
		$('.area').remove();
		$('.cluster').remove();
		$('#section').html("Aree");
		$('#section-number').hide();
		var areas = $('#ir-viewer-container');
		for (var key in robots_and_clusters_IR) {
	    if (robots_and_clusters_IR.hasOwnProperty(key)) {
				areas.append("<span class=\"area\"><span class=\"area-id\">Area " + key + "</span></span>");
			}
		}
	});
}

function showClustersView(){
	$(document).ready(function(){
		$("#change-selection").show();
		$("#change-selection").html("Torna alle Aree");
		$('.area').remove();
		$('.cluster').remove();
		$('.robot').remove();
		$('#section').html("Area");
		$('#section-number').show();
		$('#section-number').html(selected_area);
		var clusters = $('#ir-viewer-container');
		for (var key in robots_and_clusters_IR[selected_area]) {
	    if (robots_and_clusters_IR[selected_area].hasOwnProperty(key)) {
				clusters.append("<span class=\"cluster\"><span class=\"cluster-id\">Cluster " + key + "</span>" +
												"<span class=\"cluster-ir ir\">" + robots_and_clusters_IR[selected_area][key]["cluster_ir"] +
												"%</span></span>");
		  }
		}
		color();
	});
}

function showRobotsView(){
	$(document).ready(function(){
		$("#change-selection").html("Torna ai Clusters");
		$('.cluster').remove();
		$('.robot').remove();
		$('#section').html("Cluster");
		$('#section-number').html(selected_cluster);
		var robots = $('#ir-viewer-container');
		for (var key in robots_and_clusters_IR[selected_area][selected_cluster]["robots"]) {
	    if (robots_and_clusters_IR[selected_area][selected_cluster]["robots"].hasOwnProperty(key)) {
				robots.append("<span class=\"robot\"><span class=\"robot-id\">Robot " + key + "</span>" +
												"<span class=\"robot-ir ir\">" + robots_and_clusters_IR[selected_area][selected_cluster]["robots"][key] +
												"%</span></span>");
		  }
		}
		color();
	});
}
