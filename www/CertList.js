var exec = require('cordova/exec');

exports.getList = function () {
    return new Promise((resolve, reject) => {
        exec(resolve, reject, 'CertList', 'get_certlist', []);
    });
};

exports.installCert =  function() {
    return new Promise((resolve, reject) => {
        exec(resolve, reject, 'CertList', 'install', []);
    });
}
