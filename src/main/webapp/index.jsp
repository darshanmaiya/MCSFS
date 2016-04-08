<%--
   Copyright 2016 DropTheBox (Aviral Takkar, Darshan Maiya, Wei-Tsung Lin)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
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
			<div>
				<!-- Tab panes -->
				<div class="tab-content">
					<div role="tabpanel" class="tab-pane active" id="upload">
					    <form action="mcsfs" method="POST">
						  <label class="control-label">Select File *</label>
						  <input id="input-2" name="input2" type="file" class="file" />
						  <br />
						  <label for="up-passphrase">Passphrase: *</label>
						  <input id="up-passphrase" name="up-passphrase" class="form-control" />
						  <br />
                          <label for="up-adminkey">Admin Key (For replacing and deleting): *</label>
                          <input id="up-adminkey" name="up-adminkey" class="form-control" />
					   </form>
					   <br />
					   <span>
					       <strong>Note: </strong>Existing files can be replaced by providing the previous passphrase and admin key
					   </span>
				    </div>
					<div role="tabpanel" class="tab-pane" id="download">
                        <label for="down-passphrase">Passphrase: *</label>
						<div class="input-group">
                            <input id="down-passphrase" name="down-passphrase" class="form-control" />
						    <div class="input-group-btn">
						        <button id="down-btn" class="btn btn-primary">
						            <span class="glyphicon glyphicon-download-alt"></span>
						            Download
						        </button>
						    </div>
						</div>
					</div>
					<div role="tabpanel" class="tab-pane" id="delete">
					    <label for="del-passphrase">Passphrase: *</label>
                        <input id="del-passphrase" name="del-passphrase" class="form-control" />
                        <br />
                        <label for="del-adminkey">Admin Key: *</label>
                        <input id="del-adminkey" name="del-adminkey" class="form-control" />
					</div>
				</div>
			</div>
	</div>

    <script src="https://code.jquery.com/jquery-2.2.3.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
    <script src="scripts/fileinput.min.js" type="text/javascript"></script>
    <script src="scripts/index.js"></script>
    </body>
</html>