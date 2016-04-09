(function () {
	$("#frm-file-upload").submit(function (event) {
		event.preventDefault();
		event.stopPropagation();
		
		console.debug("upload");
		
		var data = new FormData($('#frm-file-upload')[0]);
		
		$.ajax({
		    url: '/mcsfs',
		    data: data,
		    cache: false,
		    contentType: false,
		    processData: false,
		    type: 'POST',
		    success: function(data) {
		    	if(data === "success")
		    		alertUtil.success("File uploaded successfully");
		    	else
		    		alertUtil.danger("File upload failed. Please try again...");
		    }
		});
		
		return false;
	});
	
	$("#frm-file-download").submit(function (event) {
		event.preventDefault();
		event.stopPropagation();
		
		console.debug("download");
		return false;
	});
	
	$("#frm-file-delete").submit(function (event) {
		event.preventDefault();
		event.stopPropagation();
		
		console.debug("delete");
		return false;
	});
})();