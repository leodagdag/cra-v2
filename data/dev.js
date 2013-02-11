db.createCollection('User');
db.User.insert({username:'lisa', password:'lisa', role:'admin', firstName:'lisa', lastName:'Simpson', trigramme:'LSN', email:'lisa@simpson.com'});
db.User.insert({username:'bart', password:'bart', role:'user', firstName:'bart', lastName:'Simpson', trigramme:'BSN', email:'bart@simpson.com'});
db.User.insert({username: 'homer', password: 'homer', role:'user', firstName:'homer', lastName:'Simpson', trigramme:'HSN', email:'homer@simpson.com'});
db.User.insert({username: 'marge', password: 'marge', role:'production', firstName:'marge', lastName:'Simpson', trigramme:'MSN', email:'marge@simpson.com'});
