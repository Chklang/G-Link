const conf = require('./gulp.conf');
const proxy = require('http-proxy-middleware');

module.exports = function () {
  return {
    server: {
      baseDir: [
        conf.paths.tmp,
        conf.paths.src
      ],
      middleware: [
        proxy('/rest', {
          target: 'http://127.0.0.1:9000',
          changeOrign: true,
          logLevel: 'debug'
        })
      ]
    },
    open: false
  };
};
