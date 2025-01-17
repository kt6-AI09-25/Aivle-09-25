const path = require('path');

module.exports = {
  entry: './stomp.js', // 메인 파일 경로
  output: {
    filename: 'stomp.min.js', // 출력 파일 이름
    path: path.resolve(__dirname, 'dist'), // 출력 디렉토리
    libraryTarget: 'umd', // UMD 형식으로 빌드
  },
  mode: 'production', // 프로덕션 모드
};
