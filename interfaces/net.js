//TODO: 
//import net from 'react-native-tcp'
var net;

function NetPrint(host, port, n) {
  net = n;
  this.timeout = 3000;
  this.host = host;
  this.port = port || 9100;
}

NetPrint.prototype.execute = function(buffer, cb) {
  var printer = net.createConnection({
    host : this.host,
    port : this.port,
    timeout: this.timeout
  }, function() {
    printer.write(buffer, null, function () {
      printer.end();
      printer.destroy();
      if (typeof cb !== "undefined") {
        cb(null);
      }
    });
  });

  printer.on('error', function (err) {
    printer.end();
    printer.destroy();
    if (typeof cb !== "undefined") {
      cb(err);
    }
  });

  printer.on('timeout', function () {
    printer.end();
    printer.destroy();
    if (typeof cb !== "undefined") {
      cb("Error: Socket Timeout");
    }
  });
};


NetPrint.prototype.isPrinterConnected = function(exists){
  var printer = net.createConnection({
    host : this.host,
    port : this.port,
    timeout: this.timeout
  }, function() {
    exists(true);
    printer.end();
  });

  printer.on('error', function (err) {
    printer.end();
    printer.destroy();
    exists(false);
  });
  
  printer.on('timeout', function () {
    printer.end();
    printer.destroy();
    exists(false);
  });
};

module.exports = NetPrint;
