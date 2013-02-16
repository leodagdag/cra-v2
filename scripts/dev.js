db.User.drop();
db.Cra.drop();
db.Day.drop();
db.HalfDay.drop();
db.Holiday.drop();
db.PartTime.drop();
db.Mission.drop();
db.Customer.drop();

/**
 * Customer
 * */
db.Customer.insert({code: 'GG', name: 'Genesis', isGenesis: true });
db.Customer.insert({code: 'CODE_C_1', name: 'SLIB', isGenesis: false});
var genesis = db.Customer.findOne({isGenesis: true});
var customer1 = db.Customer.findOne({code: 'CODE_C_1'});

/*
 * Mission
 */
db.Mission.insert({customerId: customer1._id, code: 'C1_M1', description: 'Description de la mission...', missionType: 'CUSTOMER'});
db.Mission.insert({customerId: genesis._id, code: 'AV', description: 'Avant vente', missionType: 'pre-sale'});
db.Mission.insert({customerId: genesis._id, code: 'CP', description: 'Congé payé', missionType: 'holiday' });
db.Mission.insert({customerId: genesis._id, code: 'TP', description: 'Temps partiel', missionType: 'not-paid' });
var mission_customer = db.Mission.findOne({customerId: customer1._id});
var pre_sale = db.Mission.findOne({customerId: customer1._id});
var holiday = db.Mission.findOne({customerId: customer1._id});
var part_time = db.Mission.findOne({customerId: customer1._id});

/*
 * User
 */
db.User.insert({username: 'lisa', password: 'lisa', role: 'admin', firstName: 'lisa', lastName: 'Simpson', trigramme: 'LSN', email: 'lisa@simpson.com'});
db.User.insert({username: 'bart', password: 'bart', role: 'employee', firstName: 'bart', lastName: 'Simpson', trigramme: 'BSN', email: 'bart@simpson.com'});
db.User.insert({username: 'homer', password: 'homer', role: 'employee', firstName: 'homer', lastName: 'Simpson', trigramme: 'HSN', email: 'homer@simpson.com'});
db.User.insert({username: 'marge', password: 'marge', role: 'production', firstName: 'marge', lastName: 'Simpson', trigramme: 'MSN', email: 'marge@simpson.com'});
var bart = db.User.findOne({username: 'bart'});

/*
 * Cra
 */
db.Cra.insert({year: NumberInt(2013), month: NumberInt(2), userId: bart._id, comment: 'Commentaire cra...', isValidated: false});
var cra = db.Cra.findOne({year: 2013, month: 2});

/*
 * Day
 */
db.Day.insert({craId: cra._id, _date: new Date(cra.year, cra.month - 1, NumberInt(1)),
	year: NumberInt(cra.year),
	month: NumberInt(cra.month),
	morning: {
		missionId: mission_customer._id
	},
	afternoon: {
		missionId: mission_customer._id
	},
	comment: 'Commentaire day ...'
});

