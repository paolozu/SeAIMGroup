/***********************************************
*                                              *
*                   SOCKET                     *
*                                              *
************************************************/

var webSocket = new WebSocket("ws://192.168.159.111:4444");

selected_area = null;
selected_cluster = null;
var robots_and_clusters_IR = null;
//received;

webSocket.onopen = function(message){ wsOpen(message); };
webSocket.onmessage = function(message){ wsGetMessage(message); };
webSocket.onclose = function(message){ wsClose(message); };
webSocket.onerror = function(message){ wsError(message); };

function wsOpen(message){}

function wsGetMessage(message){
	robots_and_clusters_IR = JSON.parse(message.data);
	if( selected_area == null ){
		selected_area = "areas";
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
$(document).on('click', ".area", function(){
	// selected_area is a string.
	selected_area = $(this).find('span').html().replace('Area ', '');
	var cluster_count = Object.keys(robots_and_clusters_IR[selected_area]).length;
	showClustersView(cluster_count);
});

$(document).on('click', ".cluster", function(){
	// selected_area is a string.
	selected_cluster = $(this).find('span').html().replace('Cluster ', '');
	var robots_count = Object.keys(robots_and_clusters_IR[selected_area][selected_cluster]["robots"]).length;
	showRobotsView(robots_count);
});

function showAreasView(areas_count){
	$(document).ready(function(){
		$('.area').remove();
		$('#section').html("Aree");
		$('#section-number').hide();
		var areas = $('#ir-viewer-container');
		for (var key in robots_and_clusters_IR) {
	    if (robots_and_clusters_IR.hasOwnProperty(key)) {
				areas.append("<div class=\"area\"><span class=\"area-id\">Area " + key + "</span></div>");
			}
		}
	});
}

function showClustersView(cluster_count){
	$(document).ready(function(){
		$('.area').remove();
		$('#section').html("Area");
		$('#section-number').show();
		$('#section-number').html(selected_area);
		var clusters = $('#ir-viewer-container');
		for (var key in robots_and_clusters_IR[selected_area]) {
	    if (robots_and_clusters_IR[selected_area].hasOwnProperty(key)) {
				clusters.append("<div class=\"cluster\"><span class=\"cluster-id\">Cluster " + key + "</span>" +
												"<span class=\"cluster-ir\">" + robots_and_clusters_IR[selected_area][key]["cluster_ir"] +
												"%</span></div>");
		  }
		}
		color();
	});
}

function showRobotsView(robots_count){
	$(document).ready(function(){
		$('.cluster').remove();
		$('#section').html("Cluster");
		$('#section-number').html(selected_cluster);
		var robots = $('#ir-viewer-container');
		// REMEMBER !!!
		robots.css({"margin-left" : "23px"});
		for (var key in robots_and_clusters_IR[selected_area][selected_cluster]["robots"]) {
	    if (robots_and_clusters_IR[selected_area][selected_cluster]["robots"].hasOwnProperty(key)) {
				robots.append("<div class=\"robot\"><span class=\"robot-id\">Robot " + key + "</span>" +
												"<span class=\"robot-ir\">" + robots_and_clusters_IR[selected_area][selected_cluster]["robots"][key] +
												"%</span></div>");
		  }
		}
		color();
	});
}

function color(){
	$(document).ready(function(){
	  $('.robot, .cluster').each(function(i, obj) {
	    if( $(this).find(".cluster-ir").text().split('%')[0] >= 40 )
	      $(this).css({"background-color" : "red"});
	    else
	      $(this).css({"background-color" : "green"});
	  });
	});
}
