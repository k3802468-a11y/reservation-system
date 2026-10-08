# 智慧店家預約管理系統

本專案包含：

- 客戶端線上預約網站（Spring Boot 靜態頁面）
- Spring Boot REST API 與 MySQL／Redis 環境
- Flutter 店家端 App 雛形

## 快速啟動

先確認已安裝 Docker Desktop，接著在專案根目錄執行：

```powershell
docker compose up --build
```

服務啟動後，請從瀏覽器開啟 [http://localhost:8080](http://localhost:8080)，不要直接以 `file:///` 開啟 HTML；唯有透過後端服務開啟，預約表單才能呼叫 API。

預設店家帳號：`admin`  
預設密碼：`123456`

## 已具備的流程

客人可建立預約，並透過保留在網址雜湊（`#token`）中的專屬連結查看、修改與取消預約。後端會自動建立會員資料，並將網站預約標記為 `PENDING`。

## 驗證

```powershell
.\mvnw.cmd test
```

測試包含公開預約的建立、查看、修改、取消，以及公開首頁可匿名存取的驗證。

完整系統設計請參考 [docs/system-design.md](docs/system-design.md)。
