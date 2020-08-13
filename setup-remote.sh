mkdir -m 777 -p /opt/apps/logs
sudo apt install unzip
sudo iptables -t nat -I PREROUTING -p tcp --dport 80 -j REDIRECT --to-ports 5000
sudo iptables -t nat -I OUTPUT -p tcp -o lo --dport 80 -j REDIRECT --to-ports 5000
# to open port 8080, go to EC2 instance and create a new Firewall rule:
# application: Custom, protocol:TCP	port: 8080
# may also need to uncomment this two lines below
#sudo iptables -A INPUT -p tcp -m tcp --dport 8080 --syn -j ACCEPT
#sudo iptables -I INPUT -p tcp --dport 8080 -j ACCEPT
sudo npm install -g serve
