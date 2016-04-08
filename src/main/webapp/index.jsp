<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Welcome to Multi-Cloud Secure File Store</title>
        
        <!-- Latest compiled and minified CSS -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
        <link rel="stylesheet" href="css/common.css">
    </head>
    <body>
         <nav class="navbar navbar-default navbar-fixed-top">
            <div class="container">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#mcsfs-collapsed" aria-expanded="false">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="/">MCSFS</a>
                    <p class="navbar-text">Store your files with confidence!</p>
                </div>
                <div class="collapse navbar-collapse" id="mcsfs-collapsed">
                    <ul class="nav navbar-nav navbar-right">
                        <!-- <li role="presentation"><a href="#upload"
	                        aria-controls="upload" role="tab" data-toggle="tab">Upload</a></li>
	                    <li role="presentation"><a href="#download"
	                        aria-controls="download" role="tab" data-toggle="tab">Download</a></li>
	                    <li role="presentation"><a href="#replace"
	                        aria-controls="replace" role="tab" data-toggle="tab">Replace</a></li>
	                    <li role="presentation"><a href="#delete"
	                        aria-controls="delete" role="tab" data-toggle="tab">Delete</a></li> -->
                    </ul>
                </div>
            </div>
        </nav>
        <div class="container outer-body">			
			<div>
				<!-- Nav tabs -->
				<ul class="nav nav-tabs nav-justified" role="tablist">
					<li role="presentation" class="active"><a href="#upload"
						aria-controls="upload" role="tab" data-toggle="tab">Upload</a></li>
					<li role="presentation"><a href="#download"
						aria-controls="download" role="tab" data-toggle="tab">Download</a></li>
					<li role="presentation"><a href="#replace"
						aria-controls="replace" role="tab" data-toggle="tab">Replace</a></li>
					<li role="presentation"><a href="#delete"
						aria-controls="delete" role="tab" data-toggle="tab">Delete</a></li>
				</ul>
	
				<!-- Tab panes -->
				<div class="tab-content">
					<div role="tabpanel" class="tab-pane active" id="upload">Upload</div>
					<div role="tabpanel" class="tab-pane" id="download">Download</div>
					<div role="tabpanel" class="tab-pane" id="replace">Replace</div>
					<div role="tabpanel" class="tab-pane" id="delete">Delete</div>
				</div>
			</div>
	</div>

    <script src="https://code.jquery.com/jquery-2.2.3.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
    </body>
</html>