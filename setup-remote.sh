mkdir -m 777 -p /opt/apps/logs
sudo apt install unzip
sudo iptables -t nat -I PREROUTING -p tcp --dport 80 -j REDIRECT --to-ports 5000
sudo iptables -t nat -I OUTPUT -p tcp -o lo --dport 80 -j REDIRECT --to-ports 5000
sudo npm install -g serve
