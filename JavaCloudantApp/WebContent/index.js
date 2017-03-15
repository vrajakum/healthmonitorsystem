// index.js

var REST_DATA = 'api/favorites';
var REST_LOGIN_DATA = 'api/login';
var REST_DETAIL_DATA = 'api/detail';


var KEY_ENTER = 13;
var defaultItems = [
	
];

window.onerror = function (msg, url, line) {
    alert("Message : " + msg );
    alert("url : " + url );
    alert("Line number : " + line );
 }

function addalltable(){
		  
	var batdateV=document.getElementById('hltbatid').value;
	document.getElementById('errorDiv').innerHTML="";
	
	if (batdateV == "")
	{
	document.getElementById('hltbatid').value="";
	document.getElementById('errorDiv').innerHTML=" Batch Cycle date field can't be blank";
	}
	else
		{
		
		document.getElementById('hltbatid').value="";
		
	var myTableDiv = document.getElementById("myDynamicTable");
    
    var table = document.createElement('TABLE');
    table.border='1';
    
    var tableBody = document.createElement('TBODY');
    table.appendChild(tableBody);
    
    var appname=["CABS","MEDFEES","MFAS","PW","OLTR","IPSS"];
      
    for (var i=0; i<appname.length; i++){
       
       
       var appnameV= appname[i];
       var id=batdateV+appnameV;			
	   var requestParam = '?id='+ id ;
	   
	   xhrGet(REST_DETAIL_DATA+requestParam, function(data){
		   
		   var tr = document.createElement('TR');
	       tableBody.appendChild(tr);
	       
		      var td = document.createElement('TD');
		      td.width="410";		             
		      td.appendChild(document.createTextNode(data.appname));
		      tr.appendChild(td);
	           
	           var td = document.createElement('TD');
	           td.width="410";
	           if (data.appstatus == "Not completed"){
	           td.style.color = "red";
	           }
	           td.appendChild(document.createTextNode(data.appstatus));
		       tr.appendChild(td);
		       
				   
			}, function(err){
					
					var tr = document.createElement('TR');
				       tableBody.appendChild(tr);
					var td = document.createElement('TD');
					td.width="410";
		             
				       td.appendChild(document.createTextNode(appnameV));
			           tr.appendChild(td);
			           
			           var td = document.createElement('TD');
			           td.width="410";
			           			           			           
				       td.appendChild(document.createTextNode("No Entry"));
			           tr.appendChild(td);
										
				});
       
           myTableDiv.appendChild(table);
		}
		}
}

function addtable(){
	
	var happnameV=document.getElementById('hltappid').value;
	var hbatdateV=document.getElementById('hlpbatid').value;
	
	document.getElementById('errorDiv1').innerHTML="";
	
	if(happnameV == "" && hbatdateV == "")
		{
		
		document.getElementById('errorDiv1').innerHTML="Application and Batch Cycle date field can't be blank";
		}
	
	else if(happnameV == "" )
	{
		document.getElementById('hlpbatid').value="";
		document.getElementById('errorDiv1').innerHTML="Application  field can't be blank";
	}
	
	else if (hbatdateV == "")
	{
		document.getElementById('hltappid').value="";
		document.getElementById('errorDiv1').innerHTML=" Batch Cycle date field can't be blank";
	}
	
	else
	{
	
	var id=hbatdateV+happnameV;
	
	var requestParam = '?id='+ id ;
	
	xhrGet(REST_DETAIL_DATA+requestParam, function(data){
		
		
		var table = document.getElementById('notes');
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);
	    	    
		row.insertCell(0).innerHTML= data.appname;
		row.insertCell(1).innerHTML= data.anstname;
		row.insertCell(2).innerHTML= data.compdate;
		row.insertCell(3).innerHTML= data.comptime;
	    row.insertCell(4).innerHTML= data.appstatus;
	    row.insertCell(5).innerHTML= data.prob;
		
		
	}, function(err){
		console.log(err);
		document.getElementById('errorDiv1').innerHTML = 'Record is not available and Failed to fetch the record';
			
	});
	
	document.getElementById('hltappid').value="";
	document.getElementById('hlpbatid').value="";
	
}
}
function Dailyprevious(){
	location.href = "HealthCheck.html";
}

function select(){
	if(document.getElementById('daily').checked)
	{
		location.href = "DailyHealth.html";
		
	}
	if(document.getElementById('addupt').checked)
	{
		location.href = "AddAppRec.html";
	}
	if(document.getElementById('historical').checked)
	{
		location.href = "Hisapphc.html";
	}
}

function previous(){
	location.href = "login.html";
}

function home(){
	location.href = "login.html";
}

function Appprevious(){
	location.href = "HealthCheck.html";
}
function add()
{
		
	var appnameV=document.getElementById('appid').value;
	var anstnameV=document.getElementById('anstid').value;
	var batdateV=document.getElementById('batid').value;
	var compdateV=document.getElementById('compdid').value;
	var comptimeV=document.getElementById('comptid').value;
	var appstatusV=document.getElementById('appstid').value;
	var probV=document.getElementById('probid').value;
	
	var id;
	
	var adddata={
			appname:appnameV,
			anstname:anstnameV,
			batdate:batdateV,
			compdate:compdateV,
			comptime:comptimeV,
			appstatus:appstatusV,
			prob:probV
			};
	
	var queryParams = (id == null) ? "" : "id=" + id;
	queryParams += "&appname=" + appnameV;
	queryParams += "&anstname=" + anstnameV;
	queryParams += "&batdate=" + batdateV;
	queryParams+= "&compdate="+compdateV;
	queryParams+= "&comptime="+comptimeV;
	queryParams+= "&appstatus="+appstatusV;
	queryParams+= "&prob="+probV;
	
		xhrPost(REST_DETAIL_DATA, adddata, function(item){
		document.getElementById('appid').value="";
		document.getElementById('anstid').value="";
		document.getElementById('batid').value="";
		document.getElementById('compdid').value="";
		document.getElementById('comptid').value="";
		document.getElementById('appstid').value="";
		document.getElementById('probid').value="";
		document.getElementById('errorDiv').innerHTML = "Record is stored succesfully";
	}, function(err){
		document.getElementById('appid').value="";
		document.getElementById('anstid').value="";
		document.getElementById('batid').value="";
		document.getElementById('compdid').value="";
		document.getElementById('comptid').value="";
		document.getElementById('appstid').value="";
		document.getElementById('probid').value="";
		document.getElementById('errorDiv').innerHTML = "Record is already Existing";
	});
}

function Delete()
{
		
	var appnameV=document.getElementById('appid').value;
	var batdateV=document.getElementById('batid').value;
		
    var id=batdateV+appnameV;
		
	var queryParams =  "?id=" + id;
		
	xhrDelete(REST_DETAIL_DATA+queryParams, function(item){
			document.getElementById('appid').value="";
			document.getElementById('anstid').value="";
			document.getElementById('batid').value="";
			document.getElementById('compdid').value="";
			document.getElementById('comptid').value="";
			document.getElementById('appstid').value="";
			document.getElementById('probid').value="";	
		document.getElementById('errorDiv').innerHTML = "Record is deleted Successfully";
	}, function(err){
		document.getElementById('appid').value="";
		document.getElementById('anstid').value="";
		document.getElementById('batid').value="";
		document.getElementById('compdid').value="";
		document.getElementById('comptid').value="";
		document.getElementById('appstid').value="";
		document.getElementById('probid').value="";
		document.getElementById('errorDiv').innerHTML = "Record is not deleted reach out Technical Experts";
	});
}

function update()
{
		
	var appnameV=document.getElementById('appid').value;
	var anstnameV=document.getElementById('anstid').value;
	var batdateV=document.getElementById('batid').value;
	var compdateV=document.getElementById('compdid').value;
	var comptimeV=document.getElementById('comptid').value;
	var appstatusV=document.getElementById('appstid').value;
	var probV=document.getElementById('probid').value;
	
	var id;
	
	var adddata={
			appname:appnameV,
			anstname:anstnameV,
			batdate:batdateV,
			compdate:compdateV,
			comptime:comptimeV,
			appstatus:appstatusV,
			prob:probV
			};
	
    var id=batdateV+appnameV;
		
	var queryParams =  "?id=" + id;
	queryParams += "&appname=" + appnameV;
	queryParams += "&anstname=" + anstnameV;
	queryParams += "&batdate=" + batdateV;
	queryParams+= "&compdate="+compdateV;
	queryParams+= "&comptime="+comptimeV;
	queryParams+= "&appstatus="+appstatusV;
	queryParams+= "&prob="+probV;
	
		xhrPut(REST_DETAIL_DATA+queryParams, adddata, function(item){
			document.getElementById('appid').value="";
			document.getElementById('anstid').value="";
			document.getElementById('batid').value="";
			document.getElementById('compdid').value="";
			document.getElementById('comptid').value="";
			document.getElementById('appstid').value="";
			document.getElementById('probid').value="";	
		document.getElementById('errorDiv').innerHTML = "Record is Updated Successfully";
	}, function(err){
		document.getElementById('appid').value="";
		document.getElementById('anstid').value="";
		document.getElementById('batid').value="";
		document.getElementById('compdid').value="";
		document.getElementById('comptid').value="";
		document.getElementById('appstid').value="";
		document.getElementById('probid').value="";
		document.getElementById('errorDiv').innerHTML = "Record is not updated reach out Technical Experts";
	});
}


function login()
{
	var usernameV = document.getElementById('userid').value;
	var passwordV = document.getElementById('paswrd').value;
	
	
	var id= usernameV+passwordV;
	
	var requestParam = '?id='+ id ;
	
	xhrGet(REST_LOGIN_DATA+requestParam, function(data){
		
		document.getElementById('userid').value="";
		document.getElementById('paswrd').value="";
		location.href = "HealthCheck.html";
	}, function(err){
		document.getElementById('userid').value="";
		document.getElementById('paswrd').value="";
		document.getElementById('errorDiv').innerHTML = 'Not a Valid User.\n Register your credentials.';
			
	});	
}

function newuser(){
	var usernameV = document.getElementById('userid').value;
	var passwordV = document.getElementById('paswrd').value;
	
	var userdata={
			username:usernameV,
			password:passwordV
			};
	
		xhrPost(REST_LOGIN_DATA, userdata, function(item){
		document.getElementById('userid').value="";
		document.getElementById('paswrd').value="";
		document.getElementById('errorDiv').innerHTML = "You are successfully Registered your credential in database";
		
		
	}, function(err){
		document.getElementById('errorDiv').innerHTML = "Your Credential is not stored reach out Technical Expert";
	});
}





