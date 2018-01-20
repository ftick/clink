var app = require('express')();
var http = require('http').Server(app);

var PORT = 8000;

app.get('/', function(req, res){
  res.send("Hello world!");
});

http.listen(PORT, function(){
  console.log('listening to localhost:', PORT);
});