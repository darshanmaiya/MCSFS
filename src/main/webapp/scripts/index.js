(function () {
	$("#frm-file-upload").submit(function (event) {
		event.preventDefault();
		event.stopPropagation();
		
		console.debug("upload");
		
		var fileSize = $('#file-input')[0].files[0].size;
		if(fileSize/(1024 * 1024) > 200) {
			alertUtil.danger("Max file size supported is 200 MiB. Please try again...");
			return false;
		}
		
		var data = new FormData($('#frm-file-upload')[0]);
		data.append("file-size", $('#file-input')[0].files[0].size);
		
		$.ajax({
		    url: '/mcsfs',
		    data: data,
		    cache: false,
		    contentType: false,
		    processData: false,
		    type: 'POST',
		    success: function(data) {
		    	if($.parseJSON(data).result === "success") {
		    		alertUtil.success("File uploaded successfully. Your access key is: " + $.parseJSON(data).accessKey);
		    		$("#frm-file-upload").trigger('reset');
		    	}
		    	else
		    		alertUtil.danger("File upload failed. Please try again...");
		    }
		});
		
		return false;
	});
	
	$("#frm-file-download").submit(function (event) {
		console.debug("download");
		
		return true;
	});
	
	$("#frm-file-delete").submit(function (event) {
		event.preventDefault();
		event.stopPropagation();
		
		console.debug("delete");
		
		$.ajax({
		    url: '/mcsfs',
		    data: $("#frm-file-delete").serialize(),
		    cache: false,
		    type: 'GET',
		    success: function(data) {
		    	if(data === "success") {
		    		alertUtil.success("File deleted successfully");
		    		$("#frm-file-delete").trigger('reset');
		    	}
		    	else
		    		alertUtil.danger("File delete failed. Please try again...");
		    }
		});
		
		return false;
	});
})();