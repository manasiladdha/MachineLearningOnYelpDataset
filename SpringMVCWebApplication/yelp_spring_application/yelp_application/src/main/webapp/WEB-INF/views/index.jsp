<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
<title>Help Yelp!</title>
<script
	src="http://maps.google.com/maps/api/js?key=AIzaSyBCtwMKMyAJg6JLN11DdMn7K8GbnXIPm_8&libraries=geometry"
	type="text/javascript"></script>

<script type="text/javascript">
	var map;
	var marker;
	var locations = [];
	var markers_on_map = [];
	var radius_circle = null;
	var bluemarker = null;
	var purplemarker = null;
	var locationsNotReviewed = [];

	window.onload = function() {
		initialize();
		<c:forEach var="location" items="${LatLongList}">
		locations.push([ "${location.latitude}", "${location.longitude}",
				"${location.businessName}", "${location.rating}",
				"${location.businessCity}" , "${location.sentimentalRatings}","${location.business_stars}"]);
		</c:forEach>
		<c:forEach var="location" items="${LatLongListNotReviewed}">
		locationsNotReviewed.push([ "${location.latitude}",
				"${location.longitude}", "${location.businessName}",
				"${location.rating}", "${location.businessCity}" , "${location.business_stars}" ]);
		</c:forEach>
		generateMarkers(locations);

	};

	// Cretes the map
	function initialize() {
		map = new google.maps.Map(document.getElementById('map'), {
			zoom : 7,
			center : new google.maps.LatLng("${country_latitude}",
					"${country_longitude}"),
			mapTypeId : google.maps.MapTypeId.ROADMAP
		});
		//map.setCenter()
	}

	var iconBase = 'https://maps.google.com/mapfiles/kml/paddle/';
	var icons = {
		blueIcon : {
			icon : iconBase + 'blu-circle.png'
		},
		orangeIcon : {
			icon : iconBase + 'orange-circle.png'
		}
	};

	// This function takes an array argument containing a list of marker data
	function generateMarkers(locations) {
		for (var i = 0; i < locations.length; i++) {
			new google.maps.Marker({
				position : new google.maps.LatLng(locations[i][0],
						locations[i][1]),
				map : map,
				title : locations[i][2] + "\nYour ratings : " + locations[i][3]
						+ "\nCity :" + locations[i][4] +"\nSentimental Ratings:"+ parseFloat(locations[i][5]).toFixed(2) 
						+ "\nBusiness Stars:" + locations[i][6]
			});
		}
		//generateMarkersNotReviewed(locationsNotReviewed);
		google.maps.event.addListener(map, 'click', function(event) {
			placeMarker(event.latLng);
		});

		google.maps.event.addListener(marker, 'mouseover', function() {
			infowindow.open(map, this);
		});
		// assuming you also want to hide the infowindow when user mouses-out
		marker.addListener('mouseout', function() {
			infowindow.close();
		});

	}

	// Displaying all the pins which have not been reviewed
	function generateMarkersNotReviewed(locationsNotReviewed) {
		for (var i = 0; i < locationsNotReviewed.length; i++) {
			new google.maps.Marker({
				position : new google.maps.LatLng(locationsNotReviewed[i][0],
						locationsNotReviewed[i][1]),
				map : map,
				icon : icons.purpleIcon.icon,
				title : locationsNotReviewed[i][2] + "\nCity :"
						+ locationsNotReviewed[i][3]
			});
		}

		google.maps.event.addListener(map, 'click', function(event) {
			placeMarker(event.latLng);
			//alert("Test: Not reviewed");
		});

		google.maps.event.addListener(marker, 'mouseover', function() {
			infowindow.open(map, this);
		});
		// assuming you also want to hide the infowindow when user mouses-out
		marker.addListener('mouseout', function() {
			infowindow.close();
		});

	}

	function placeMarker(location) {
		if (bluemarker != null) {
			bluemarker.setMap(null);

		}
		bluemarker = new google.maps.Marker({
			position : location,
			map : map,
			icon : icons.blueIcon.icon
		});

		//google.maps.event.addListener(map, 'click', showCloseLocations);
		showCloseLocations(location)
	}

	function showCloseLocations(e) {
		var i;

		var radius_km = 50000;

		var all_locations = locationsNotReviewed;

		//remove all radii and markers from map before displaying new ones
		if (radius_circle) {
			radius_circle.setMap(null);
			radius_circle = null;
		}

		for (i = 0; i < markers_on_map.length; i++) {
			if (markers_on_map[i]) {
				markers_on_map[i].setMap(null);
				markers_on_map[i] = null;
			}
		}

		var address_lat_lng = e;
		radius_circle = new google.maps.Circle({
			center : address_lat_lng,
			radius : radius_km * 1,
			clickable : false,
			map : map
		});
		if (radius_circle)
			map.fitBounds(radius_circle.getBounds());
		for (var j = 0; j < all_locations.length; j++) {
			(function(locationsNotReviewed) {
				var marker_lat_lng = new google.maps.LatLng(
						locationsNotReviewed[0], locationsNotReviewed[1]);
				var distance_from_location = google.maps.geometry.spherical
						.computeDistanceBetween(address_lat_lng, marker_lat_lng); //distance in meters between your location and the marker
				if (distance_from_location <= radius_km * 1) {

					var new_marker = new google.maps.Marker({
						position : marker_lat_lng,
						map : map,
						title : locationsNotReviewed[2]
								+ "\nPredicted Stars : "
								+ locationsNotReviewed[3] + "\nCity :"
								+ locationsNotReviewed[4],
						icon : icons.orangeIcon.icon
					});
					markers_on_map.push(new_marker);
				}
			})(all_locations[j]);
		}
	}
</script>
<style>
#map {
	height: 600px;
	width: 1300px;
}


ul {
    list-style-type: none;
    margin: 0;
    padding: 0;
    overflow: hidden;
    background-color: #333;
}

li {
    float: left;
    display: block;
    color: white;
    text-align: center;
    padding: 14px 16px;
    text-decoration: none;
}

li a {
    display: block;
    color: white;
    text-align: center;
    padding: 14px 16px;
    text-decoration: none;
}

li a:hover {
    background-color: #111;
}
</style>

</head>
<body bgcolor="black">
	<ul>
		<li>Hello, &nbsp;${user.username}</li>
		<li>Seems you like &nbsp;${user.hotState}&nbsp; a lot!</li>
		<li>You have reviewed ${user.reviewCount} businesses over here</li>
		<li>Let's see what else you may like</li>
	</ul>
	<div id="map"></div>

</body>
</html>
