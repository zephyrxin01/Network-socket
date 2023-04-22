# Network-socket
## 功能介紹
- 以JAVA android去呈現Socket通訊
- 像一般的聊天室一樣可以接受多個client同時在線聊天。
- 用的是TCP ipv4相較UDP為較穩定的連接方式
- 特點
  - Multithread
		1.可以不斷listen新的client
		2. 接收client訊息，並將訊息broadcast到連線中的clients
		3. 若client leave，從client list 移除並告知所有連線中的clients
	- Android GUI
	  - 介面1 :
	    - 提示使用者登入姓名、欲連線server IP 和 server port
	  - 介面2 :
		  - Message Box 顯示連線狀態、chat box
		  - Connect to server button 重新整理連線狀態
		  - Leave button 離開聊天室(介面2)
		  - Send button 傳送json格式的message給server，並且經由server broadcast 給所有client
	- JAVA Thread
		  - readThread: 如果有成功讀到訊息，就會append到tvMessages。
		  - sendThread: sendBtn被觸發時會執行，若是成功，則會先傳輸CLIENT_NAME，然後再傳送要傳送的message，送出後會將原本etMessage內的文字清掉，以便下一次的傳輸。
		  - leaveBtn被觸發則會傳送bye到server，並且離開第二頁回到主頁的登入頁面
## demo
- ![image](https://user-images.githubusercontent.com/69389836/233754835-38cc7192-0687-4354-b247-e1cc75acf331.png) 
- ![image](https://user-images.githubusercontent.com/69389836/233754864-40bc4d6d-0b5b-451a-b39b-dbcb76f0bfc7.png)
- ![image](https://user-images.githubusercontent.com/69389836/233754879-da4ec7fb-24a9-4133-b818-03729b296c99.png)
- ![image](https://user-images.githubusercontent.com/69389836/233754787-de9ca63b-28ef-423f-ac15-6957b5ec00ab.png)
- ![image](https://user-images.githubusercontent.com/69389836/233754802-09771198-ad85-4dc0-9489-07123152df21.png)
- ![image](https://user-images.githubusercontent.com/69389836/233754810-03f6971d-982b-45f7-876d-ff6591e285af.png)

