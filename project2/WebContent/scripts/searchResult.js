function handleSearchResult(resultData) {
	console.log("handleSearchResult: populating movie rating table from resultData");
	
	var searchResultsTableBodyElement = jQuery("#results_table_body");
	for (var i = 0; i < resultData.length; i++) {
		var rowHTML = "";
		rowHTML += "<tr>";
		rowHTML += "<td>" + "<a href = MoviePage.html?m_id=" + resultData[i]["movie_id"]
					+ ">" + resultData[i]["movie_title"] + "</a></td>";
		rowHTML += "<td>" + resultData[i]["movie_year"] + "</td>";
		rowHTML += "<td>" + resultData[i]["movie_director"] + "</td>";
		rowHTML += "<td>" + resultData[i]["genre_list"] + "</td>";
		var starList = resultData[i]["star_list"];
		var stars = starList.split(", ");
		rowHTML += "<td>";
		for (var j = 0; j < stars.length; j++) {
			rowHTML += "<a href = \"StarPage.html?s_name=" + stars[j] + "\">" + stars[j] + "</a>";
		}
		rowHTML += "</td>";
		rowHTML += "<td><button type=\"submit\"><i class=\"fa fa-plus-square\">" 
				+ "</button></td>";
		rowHTML += "</tr>"
		searchResultsTableBodyElement.append(rowHTML);
	}
}

function handlePageButtons(qs) {
	var pageButton = jQuery("#page_form");
	var params = qs.split('&');
	var newQS = "SearchResult.html?";
	var pageNum = 1;
	var pageHTML = ""
	for(var i = 0; i < params.length; i++){
		var attr = params[i].split("=");
		if(!(attr[0] == "page")){
			newQS += params[i] + "&";
		} else {
			pageNum = attr[1];
		}
	}
	
	var prevPage = (parseInt(pageNum) - 1);
	if (prevPage > 0) {
		pageHTML += "<a href=\"" + newQS + "page=" + prevPage + "\" class=\"previous\"> &#8249;</a>";
	}
	var nextPage = (parseInt(pageNum) + 1);
	pageHTML += "<a href=\"" + newQS + "page=" + nextPage + "\" class=\"next\"> &#8250;</a>";
	
	pageButton.append(pageHTML);
}

function handleTitleSortQuery(qs) {
	var SortByTitle = jQuery("#sort_title_head");
	var TitleHTML = "";
	var params = qs.split('&');
	var newQS = "SearchResult.html?";
	for(var i = 0; i < params.length; i++){
		var attr = params[i].split("=");
		if(!(attr[0] == "sortTitle") && !(attr[0] == "sortYear")){
			newQS += params[i] + "&";
		} 
	}
	TitleHTML += "<a href=\"" + newQS + "sortTitle=true\">Title</a>"
	
	SortByTitle.append(TitleHTML);
}

function handleYearSortQuery(qs) {
	var SortByYear = jQuery("#sort_year_head");
	var YearHTML = "";
	var params = qs.split('&');
	var newQS = "SearchResult.html?";
	for(var i = 0; i < params.length; i++){
		var attr = params[i].split("=");
		if(!(attr[0] == "sortTitle") && !(attr[0] == "sortYear")){
			newQS += params[i] + "&";
		} 
	}
	YearHTML += "<a href=\"" + newQS + "sortYear=true\">Year</a>"
	
	SortByYear.append(YearHTML);
}

function submitItemsForm(formSubmitEvent, qs) {
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

handlePageButtons(queryString);
handleTitleSortQuery(queryString);
handleYearSortQuery(queryString);

jQuery.ajax({
	  dataType: "json",
	  method: "GET",
	  url: "/project2/Search",
	  data: queryString,
	  success: (resultData) => handleSearchResult(resultData)
});

jQuery("#items_form").submit((event) => submitItemsForm(event, queryString));

