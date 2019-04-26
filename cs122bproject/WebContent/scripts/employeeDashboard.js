
function handleTableResult(resultData) {
	console.log("handleMovieResult: populating movie rating table from resultData");

	// populate the star table
	var schemaTableBodyElement = jQuery("#table_metadata_body");
	for (var i = 0; i < Math.min(10, resultData.length); i++) {
		var rowHTML = "";
		rowHTML += "<tr>";
		rowHTML += "<td>" + resultData[i]["table_category"] + "</td>";
		var attributeList = resultData[i]["attribute_list"];
		var attributes = attributeList.split(", ");
		rowHTML += "<td>";
		for (var j = 0; j < attributes.length; j++) {
			rowHTML += attributes[j] + "<br>";
		}
		rowHTML += "</td>";
		schemaTableBodyElement.append(rowHTML);
	}
}

// makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
	  dataType: "json",
	  method: "GET",
	  url: "/cs122bproject/EmployeeDashboard",
	  success: (resultData) => handleTableResult(resultData)
});