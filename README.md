# PoS
##  **Model Classes**

- `Client`: `id`, `name`
- `Product`: `id`, `barcode`, `clientId`, `name`, `mrp`, `imageUrl`
- `Inventory`: `id`, `productId`, `quantity`

---

## 🔗 **API List**

### 🔹 Client APIs

```
POST   /api/client            → Add a client
GET    /api/client            → List all clients
PUT    /api/client/{id}       → Update client info

```

### 🔹 Product APIs

```
POST   /api/product           → Add product (single or TSV bulk upload)
GET    /api/product           → List products (filters: client, sku, name)
PUT    /api/product/{id}      → Edit product details

```

### 🔹 Inventory APIs

```
GET    /api/inventory         → Get inventory snapshot
PUT    /api/inventory/{id}    → Update inventory for a product

```

