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
- ![螢幕擷取畫面_20230113_024722](https://user-images.githubusercontent.com/69389836/233754639-c0d5986e-4350-47c7-b3a3-f315899d49a9.png)
- ![螢幕擷取畫面_20230113_024730](https://user-images.githubusercontent.com/69389836/233754645-acb363cf-dd1e-4508-a53e-643bfed456bd.png)
- ![螢幕擷取畫面_20230113_024858](https://user-images.githubusercontent.com/69389836/233754651-39b243e4-8c8c-4c24-884d-43a0191c5657.png)
- ![螢幕擷取畫面_20230113_024907](https://user-images.githubusercontent.com/69389836/233754654-5244050e-a639-4d3e-95a6-54b432f97ca7.png)
- ![螢幕擷取畫面_20230113_024914](https://user-images.githubusercontent.com/69389836/233754656-42a6a4f9-9f91-409b-957c-0126104c7a05.png)
- ![螢幕擷取畫面_20230113_025017](https://user-images.githubusercontent.com/69389836/233754660-51c8f1ca-d539-4ab4-886a-3cca9c08a8e6.png)
- ![螢幕擷取畫面_20230113_025026](https://user-images.githubusercontent.com/69389836/233754668-7d99b127-73ae-47f4-934f-7f246f87bc45.png)
- ![螢幕擷取畫面_20230113_025226](https://user-images.githubusercontent.com/69389836/233754673-5089b608-9e6f-45a9-8780-3c9e030f2861.png)
- ![螢幕擷取畫面_20230113_025233](https://user-images.githubusercontent.com/69389836/233754678-b1c8795d-a4a5-4693-9da0-88bad31907f1.png)
- ![螢幕擷取畫面_20230113_025259](https://user-images.githubusercontent.com/69389836/233754682-6799c825-6a28-43ab-ac7d-15a1b37f4130.png)
