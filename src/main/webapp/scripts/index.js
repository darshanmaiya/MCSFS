/*
    Copyright (C) 2016 DropTheBox (Aviral Takkar, Darshan Maiya, Wei-Tsung Lin)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
		
		$("#frm-file-upload").find("input, button").prop('disabled', true);
		
		alertUtil.warning("Uploading file...")
		$(".alert-warning").append("<span id='spinner'></span>");
		var target = document.getElementById('spinner');
		var opts = {
				  lines: 9 // The number of lines to draw
				, length: 6 // The length of each line
				, width: 2 // The line thickness
				, radius: 2 // The radius of the inner circle
				, scale: 1 // Scales overall size of the spinner
				, corners: 1 // Corner roundness (0..1)
				, color: '#000' // #rgb or #rrggbb or array of colors
				, opacity: 0.25 // Opacity of the lines
				, rotate: 0 // The rotation offset
				, direction: 1 // 1: clockwise, -1: counterclockwise
				, speed: 1 // Rounds per second
				, trail: 60 // Afterglow percentage
				, fps: 20 // Frames per second when using setTimeout() as a fallback for CSS
				, zIndex: 2e9 // The z-index (defaults to 2000000000)
				, className: 'spinner' // The CSS class to assign to the spinner
				, top: '-15px' // Top position relative to parent
				, left: '50%' // Left position relative to parent
				, shadow: false // Whether to render a shadow
				, hwaccel: false // Whether to use hardware acceleration
				, position: 'relative' // Element positioning
				};
		var spinner = new Spinner(opts).spin(target);
		
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
		    	else {
		    		alertUtil.danger("File upload failed. Please try again...");
		    	}

	    		$("#frm-file-upload").find("input, button").prop('disabled', false);
		    	spinner.stop();
		    },
		    error: function (data) {
		    	alertUtil.danger("File upload failed. Please try again...");
	    		$("#frm-file-upload").find("input, button").prop('disabled', false);
	    		spinner.stop();
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
		    url: '/mcsfs' + '?' + $.param({"del-passphrase": $("#del-passphrase").val(), "del-access-key": $("#del-access-key").val()}),
		    cache: false,
		    type: 'DELETE',
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