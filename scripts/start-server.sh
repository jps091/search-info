echo "--------------- 서버 배포 시작 -----------------"
docker stop search-info-server || true
docker rm search-info-server || true
docker rmi 730335533510.dkr.ecr.ap-northeast-2.amazonaws.com/search-info-server:latest || true
docker pull 730335533510.dkr.ecr.ap-northeast-2.amazonaws.com/search-info-server:latest
docker run -d --name search-info-server -p 8080:8080 730335533510.dkr.ecr.ap-northeast-2.amazonaws.com/search-info-server:latest
echo "--------------- 서버 배포 끝 -----------------"