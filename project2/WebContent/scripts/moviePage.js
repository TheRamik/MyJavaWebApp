function handleMovieResult(resultData) {
	console.log("handleSearchResult: populating movie rating table from resultData");
	
	var searchResultsTableBodyElement = jQuery("#results_table_body");
	for (var i = 0; i < resultData.length; i++) {
		var rowHTML = "";
		rowHTML += "<tr>";
		rowHTML += "<td>" + "<a href = MoviePage.html?m_id=" + resultData[i]["movie_id"]
					+ ">" + resultData[i]["movie_title"] + "</a></td>";
		rowHTML += "<td>" + resultData[i]["movie_year"] + "</td>";
		rowHTML += "<td>" + resultData[i]["movie_director"] + "</td>";
		var genreList = resultData[i]["genre_list"];
		var genres = genreList.split(", ");
		rowHTML += "<td>";
		for (var j = 0; j < genres.length; j++) {
			rowHTML += "<a href = \"showMovies.html?genre=" + genres[j] + "&title=\">" + genres[j] + "</a>";
		}
		rowHTML += "</td>";
		var starList = resultData[i]["star_list"];
		var stars = starList.split(", ");
		rowHTML += "<td>";
		for (var j = 0; j < stars.length; j++) {
			rowHTML += "<a href = \"StarPage.html?s_name=" + stars[j] + "\">" + stars[j] + "</a>";
		}
		rowHTML += "</td>";
		rowHTML += "<td><button type=\"submit\"><i class=\"fa fa-plus-square\">" 
				+ "</button></td>";
		//rowHTML += "<td> <form id=\"cart_form\"" + "action=\"AddToCart?m_id=" +
		//			resultData[i]["movie_id"] + "\">" +
		//			"<button type=\"submit\"><i class=\"fa fa-plus-square\">" +
		//			"</button></td>";
		rowHTML += "</tr>"
		searchResultsTableBodyElement.append(rowHTML);
	}
}

function submitToCartForm(formSubmitEvent, qs) {
	formSubmitEvent.preventDefault();
	
	var params = qs.split('&');
	var x = false;
	var newQS = ""
	for(var i = 0; i < params.length; i++){
		var attr = params[i].split("=");
		if(!(attr[0] == "items")){
			newQS += params[i] + "&";
		}
	}
	
	var itemNum = jQuery("#items_form").serialize();
	
	newQS += itemNum;
	window.location.search = newQS;
}

var queryString = window.location.search.substring(1);

jQuery.ajax({
	  dataType: "json",
	  method: "GET",
	  url: "/project2/MoviePage",
	  data: queryString,
	  success: (resultData) => handleMovieResult(resultData)
});

jQuery("#cart_form").submit((event) => submitToCartForm(event, queryString));

