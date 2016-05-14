<%--
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
--%>

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
        <link href="css/fileinput.min.css" media="all" rel="stylesheet" type="text/css" />
        <link rel="stylesheet" href="css/common.css">
        
        <link rel="icon" href="favicon.ico?v2" />
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
                    <!-- Nav tabs -->
                    <ul class="nav navbar-nav nav-pills navbar-right">
                        <li role="presentation" class="active"><a href="#upload"
	                        aria-controls="upload" role="tab" data-toggle="tab">Upload</a></li>
	                    <li role="presentation"><a href="#download"
	                        aria-controls="download" role="tab" data-toggle="tab">Download</a></li>
	                    <li role="presentation"><a href="#delete"
	                        aria-controls="delete" role="tab" data-toggle="tab">Delete</a></li>
                    </ul>
                </div>
            </div>
        </nav>
        <div class="container outer-body">
            <jsp:include page="WEB-INF/jsp/alertUtil.jsp" />
			<div>
				<!-- Tab panes -->
				<div class="tab-content">
					<div role="tabpanel" class="tab-pane active" id="upload">
					    <form id="frm-file-upload" action="/mcsfs" method="POST" enctype="multipart/form-data">
					        <label for="up-passphrase">Passphrase: *</label>
                            <input id="up-passphrase" name="up-passphrase" required class="form-control" />
                            <br />
							<label class="control-label">Select File *</label>
							<input id="file-input" name="file-input" required type="file" class="file" />
					    </form>
				    </div>
					<div role="tabpanel" class="tab-pane" id="download">
					    <form id="frm-file-download" action="/mcsfs" method="GET">
	                        <label for="down-access-key">Access Key: *</label>
							<div class="input-group">
	                            <input required id="down-access-key" name="down-access-key" class="form-control" />
							    <div class="input-group-btn">
							        <button type="submit" id="btn-down" class="btn btn-primary">
							            <span class="glyphicon glyphicon-download-alt"></span>
							            Download
							        </button>
							    </div>
							</div>
						</form>
					</div>
					<div role="tabpanel" class="tab-pane" id="delete">
					    <form id="frm-file-delete" action="/mcsfs" method="GET">
						    <label for="del-passphrase">Passphrase: *</label>
	                        <input id="del-passphrase" name="del-passphrase" required class="form-control" />
	                        <br />
	                        <label for="del-access-key">Access Key: *</label>
	                        <input id="del-access-key" name="del-access-key" required class="form-control" />
	                        <br />
	                        <button type="submit" id="btn-delete" class="btn btn-primary btn-lg btn-block">
	                           <span class="glyphicon glyphicon-floppy-remove"></span> Delete
	                        </button>
                        </form>
					</div>
				</div>
			</div>
	</div>

    <script src="https://code.jquery.com/jquery-2.2.3.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/spin.js/2.3.2/spin.min.js" type="text/javascript"></script>
    <script src="scripts/fileinput.min.js" type="text/javascript"></script>
    <script src="scripts/alertUtil.js"></script>
    <script src="scripts/index.js"></script>
    </body>
</html>