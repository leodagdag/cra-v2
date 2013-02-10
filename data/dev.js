db.createCollection('user')
db.user.insert({username:'lisa', password:'lisa', role:'admin', firstName:'lisa', lastName:'Simpson', trigramme:'LSN', email:'lisa@simpson.com'});
db.user.insert({username:'bart', password:'bart', role:'user', firstName:'bart', lastName:'Simpson', trigramme:'BSN', email:'bart@simpson.com'});
db.user.insert({username: 'homer', password: 'homer', role:'user', firstName:'homer', lastName:'Simpson', trigramme:'HSN', email:'homer@simpson.com'});
db.user.insert({username: 'maggie', password: 'maggie', role:'production', firstName:'maggie', lastName:'Simpson', trigramme:'MSN', email:'maggie@simpson.com'});
