Application uses H2 database that is stored in a file so it persists data even in case of application restart.
Initial schema and example data is inserted at startup if there is no data in the db yet. Application starts on default port 8080.

Endpoints:
`GET @ /api/budgeting` returns all registers with their balances

`POST @ /api/budgeting` with example body:
```
{
	"amount": 1000,
	"name": "Car Savings"
}
```

`POST @ /api/budgeting/{id}/deposit` with example body: 
```
{
"amount": 10.0 
}
```
will deposit to category with id with amount passes in body, in this case 10. Amount cannot be less or equal to zero.

`POST @ /api/budgeting/{id}/transfer` with example body: 
```
{ 
"targetId": 3, "amount": 10.0 
} 
```
will transfer amount from category with id equal to path variable to category with id = targetId from request body. Both cattegory ids should be first retrieved using via GET @ /api/budgeting id and targetId cannot be the same. Amount cannot be less or equal to zero.

`DELETE @ /api/budgeting/{id}` 
will delete category with id equal to path variable. Balance cannot be more than zero
