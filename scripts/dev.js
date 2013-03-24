var ONE = NumberInt(1);
var YEAR = NumberInt(2013);
var JANUARY_JS = NumberInt(0);
var FEBRUARY_JS = NumberInt(1);
var MARCH_JS = NumberInt(2);
var APRIL_JS = NumberInt(3);
var MAY_JS = NumberInt(4);
var JUNE_JS = NumberInt(5);
var JULY_JS = NumberInt(6);
var AUGUST_JS = NumberInt(7);
var SEPTEMBER_JS = NumberInt(8);
var OCTOBER_JS = NumberInt(9);
var NOVEMBER_JS = NumberInt(10);
var DECEMBER_JS = NumberInt(11);

var JANUARY = NumberInt(1);
var FEBRUARY = NumberInt(2);
var MARCH = NumberInt(3);
var APRIL = NumberInt(4);
var MAY = NumberInt(5);
var JUNE = NumberInt(6);
var JULY = NumberInt(7);
var AUGUST = NumberInt(8);
var SEPTEMBER = NumberInt(9);
var OCTOBER = NumberInt(10);
var NOVEMBER = NumberInt(11);
var DECEMBER = NumberInt(12);

db.Parameter.drop();
db.User.drop();
db.Vehicle.drop();
db.Cra.drop();
db.Day.drop();
db.HalfDay.drop();
db.Absence.drop();
db.PartTime.drop();
db.Claim.drop();
db.Mission.drop();
db.Customer.drop();

/**
 * Parameter
 */
db.Parameter.insert({
	_startDate: new Date(YEAR, JANUARY_JS, 1),
	active: true,
	_car: {
		0: '0.1',
		5: '5.5',
		8: '8.8',
		11: '11'
	},
	_motorcycle: {
		"0": "1.1",
		"501": "5.01"
	},
	_zoneAmount: '4.70'
});
/**
 * Customer
 * */
db.Customer.insert({code: 'GG', name: 'Genesis', isGenesis: true });
db.Customer.insert({code: 'CODE_C_1', name: 'Client 1', isGenesis: false});
db.Customer.insert({code: 'CODE_C_2', name: 'Client 2', isGenesis: false});
db.Customer.insert({code: 'CODE_C_3', name: 'Client 3', isGenesis: false});
var genesis = db.Customer.findOne({isGenesis: true});
var customer1 = db.Customer.findOne({code: 'CODE_C_1'});
var customer2 = db.Customer.findOne({code: 'CODE_C_2'});
var customer3 = db.Customer.findOne({code: 'CODE_C_3'});

/*
 * Mission
 */
db.Mission.insert({customerId: customer1._id, code: 'C1_M1 (REAL)', description: 'Description de la mission...', allowanceType: 'REAL', _distance: '43', missionType: 'customer', isClaimable: true, _startDate: new Date(YEAR, JANUARY_JS, ONE), _endDate: new Date(YEAR, APRIL_JS, NumberInt(30))});
db.Mission.insert({customerId: customer1._id, code: 'C1_M2 (ZONE)', description: 'Description de la mission...', allowanceType: 'ZONE', missionType: 'customer', isClaimable: true, _startDate: new Date(YEAR, FEBRUARY_JS, ONE), _endDate: new Date(YEAR, DECEMBER_JS, NumberInt(31))});
db.Mission.insert({customerId: customer2._id, code: 'C2_M1 (REAL)', description: 'Description de la mission...', allowanceType: 'REAL', _distance: '72', missionType: 'customer', isClaimable: true, _startDate: new Date(YEAR, FEBRUARY_JS, ONE), _endDate: new Date(YEAR, APRIL_JS, NumberInt(30))});
db.Mission.insert({customerId: customer3._id, code: 'C3_M1 (ZONE)', description: 'Description de la mission...', allowanceType: 'ZONE', missionType: 'customer', isClaimable: true, _startDate: new Date(YEAR, MARCH_JS, ONE), _endDate: new Date(YEAR, NOVEMBER_JS, NumberInt(30))});
db.Mission.insert({customerId: genesis._id, code: 'AV', description: 'Avant vente', missionType: 'pre_sale', isClaimable: true, _startDate: new Date(YEAR, JANUARY_JS, ONE)});
db.Mission.insert({customerId: genesis._id, code: 'CP', description: 'Congé payé', missionType: 'holiday', absenceType: 'CP', _startDate: new Date(YEAR, JANUARY_JS, ONE) });
db.Mission.insert({customerId: genesis._id, code: 'RTTE', description: 'RTT Employeur', missionType: 'holiday', absenceType: 'RTT', _startDate: new Date(YEAR, JANUARY_JS, ONE) });
db.Mission.insert({customerId: genesis._id, code: 'RTTS', description: 'RTT Salarié', missionType: 'holiday', absenceType: 'RTT', _startDate: new Date(YEAR, JANUARY_JS, ONE) });
db.Mission.insert({customerId: genesis._id, code: 'AE', description: 'Absence exceptionnelle', missionType: 'holiday', absenceType: 'CP', _startDate: new Date(YEAR, JANUARY_JS, ONE) });
db.Mission.insert({customerId: genesis._id, code: 'TP', description: 'Temps partiel', missionType: 'not_paid', _startDate: new Date(YEAR, JANUARY_JS, 1)});
var mission1_customer1 = db.Mission.findOne({customerId: customer1._id, code: 'C1_M1 (REAL)'});
var mission2_customer1 = db.Mission.findOne({customerId: customer1._id, code: 'C1_M2 (ZONE)'});
var mission1_customer2 = db.Mission.findOne({customerId: customer2._id, code: 'C2_M1 (REAL)'});
var mission1_customer3 = db.Mission.findOne({customerId: customer3._id, code: 'C3_M1 (ZONE)'});
var pre_sale = db.Mission.findOne({customerId: genesis._id, missionType: 'pre_sale' });
var holiday = db.Mission.findOne({customerId: genesis._id, code: 'CP', missionType: 'holiday'});
var rtte = db.Mission.findOne({customerId: genesis._id, code: 'RTTE', missionType: 'holiday'});
var rtts = db.Mission.findOne({customerId: genesis._id, code: 'RTTS', missionType: 'holiday'});
var ae = db.Mission.findOne({customerId: genesis._id, code: 'AE', missionType: 'holiday'});
var part_time = db.Mission.findOne({customerId: genesis._id, code: 'TP', missionType: 'not_paid' });

/*
 * User
 */
db.User.insert({username: 'lisa', password: '7RT0pNfs3bba6OVJADALHg==', role: 'admin', firstName: 'lisa', lastName: 'Simpson', trigramme: 'LSN', email: 'lisa@simpson.com'});
db.User.insert({username: 'bart', password: '9UFGo/yCqxflJlaVsj9kaw==', role: 'employee', firstName: 'bart', lastName: 'Simpson', trigramme: 'BSN', email: 'bart@simpson.com',
	affectedMissions: [
		{_startDate: mission1_customer1._startDate, _endDate: mission1_customer1._endDate, missionId: mission1_customer1._id},
		{_startDate: mission2_customer1._startDate, _endDate: mission2_customer1._endDate, missionId: mission2_customer1._id},
		{_startDate: pre_sale._startDate, _endDate: pre_sale._endDate, missionId: pre_sale._id},
		{_startDate: holiday._startDate, _endDate: holiday._endDate, missionId: holiday._id},
		{_startDate: rtte._startDate, _endDate: rtte._endDate, missionId: rtte._id},
		{_startDate: rtts._startDate, _endDate: rtts._endDate, missionId: rtts._id},
		{_startDate: ae._startDate, _endDate: ae._endDate, missionId: ae._id},
		{_startDate: part_time._startDate, _endDate: part_time._endDate, missionId: part_time._id}
	]});
db.User.insert({username: 'moe', password: 'fzMzTUwvbdb/xwGUTOwvHA==', role: 'employee', firstName: 'Moe', lastName: 'Szyslak', trigramme: 'MSK', email: 'moe@szyslak.com',
	affectedMissions: [
		{_startDate: mission1_customer2._startDate, _endDate: mission1_customer2._endDate, missionId: mission1_customer2._id},
		{_startDate: mission1_customer3._startDate, _endDate: mission1_customer3._endDate, missionId: mission1_customer3._id},
		{_startDate: pre_sale._startDate, _endDate: pre_sale._endDate, missionId: pre_sale._id},
		{_startDate: holiday._startDate, _endDate: holiday._endDate, missionId: holiday._id},
		{_startDate: rtte._startDate, _endDate: rtte._endDate, missionId: rtte._id},
		{_startDate: rtts._startDate, _endDate: rtts._endDate, missionId: rtts._id},
		{_startDate: ae._startDate, _endDate: ae._endDate, missionId: ae._id},
		{_startDate: part_time._startDate, _endDate: part_time._endDate, missionId: part_time._id}
	]});
db.User.insert({username: 'ned', password: '9o2q0Ymy//0LjKteNuydlg==', role: 'employee', firstName: 'Ned', lastName: 'Flanders', trigramme: 'NFS', email: 'ned@flanders.com', isManager: true});
db.User.insert({username: 'seymour', password: '/mQGPJqqQBru0Shwi1/MeQ==', role: 'employee', firstName: 'Seymour', lastName: 'Skinner', trigramme: 'SSR', email: 'seymour@skinner.com', isManager: true});
db.User.insert({username: 'marge', password: '9FC56hi/fYLa0SL3KcCTXw==', role: 'production', firstName: 'marge', lastName: 'Simpson', trigramme: 'MSN', email: 'marge@simpson.com'});
var bart = db.User.findOne({username: 'bart'});

/*
 * Vehicle
 */
db.Vehicle.insert({
 userId: bart._id,
 vehicleType: "car",
 power: NumberInt(5),
 brand: "ALFA_ROMEO",
 matriculation: "AA-123-AA",
 _startDate: new Date(YEAR, MARCH_JS, ONE),
 active: true
 });
/*
 * Cra
 */
db.Cra.insert({year: YEAR, month: MARCH, userId: bart._id, comment: 'Commentaire cra...', isValidated: false});
var cra = db.Cra.findOne({year: YEAR, month: MARCH});

/*
 * Absence
 */
db.Absence.insert({
	userId: bart._id,
	missionId: holiday._id,
	startMorning: true,
	startAfternoon: true,
	endMorning: true,
	endAfternoon: true,
	_nbDays: "3",
	comment: "Comment absence...",
	_startDate: new Date(cra.year, MARCH_JS, NumberInt(12)),
	_endDate: new Date(cra.year, MARCH_JS, NumberInt(14))
});

/*
 * Day
 */
/* 2013/03/01 */
db.Day.insert({
	craId: cra._id,
	userId: bart._id,
	year: YEAR,
	month: MARCH,
	_date: new Date(YEAR, MARCH_JS, ONE),
	morning: {
		missionId: mission1_customer1._id
	},
	afternoon: {
		missionId: mission1_customer1._id
	},
	comment: 'Comment day ...'
});
db.Claim.insert({
	userId: bart._id,
	year: YEAR,
	month: MARCH,
	_date: new Date(YEAR, MARCH_JS, ONE),
	missionId: mission1_customer1._id,
	claimType: "MISSION_ALLOWANCE",
	_amount: "4.70"
});
/* 2013/03/05 */
db.Day.insert({
	craId: cra._id,
	userId: bart._id,
	year: YEAR,
	month: MARCH,
	_date: new Date(YEAR, MARCH_JS, NumberInt(5)),
	morning: {
		missionId: mission1_customer1._id
	},
	afternoon: {
		missionId: mission1_customer1._id
	}
});
db.Claim.insert({
	userId: bart._id,
	year: YEAR,
	month: MARCH,
	_date: new Date(YEAR, MARCH_JS, NumberInt(5)),
	missionId: mission1_customer1._id,
	claimType: "MISSION_ALLOWANCE",
	_amount: "4.70"
});
/* 2013/03/06 */
db.Day.insert({
	craId: cra._id,
	userId: bart._id,
	year: YEAR,
	month: MARCH,
	_date: new Date(YEAR, MARCH_JS, NumberInt(6)),
	morning: {
		missionId: pre_sale._id
	},
	afternoon: {
		missionId: pre_sale._id
	},
	comment: 'Comment day ...'
});
db.Claim.insert({
	userId: bart._id,
	year: YEAR,
	month: MARCH,
	_date: new Date(YEAR, MARCH_JS, NumberInt(6)),
	missionId: mission1_customer1._id,
	claimType: "MISSION_ALLOWANCE",
	_amount: "4.70"
});
/* 2013/03/07 */
db.Day.insert({
	craId: cra._id,
	userId: bart._id,
	year: YEAR,
	month: MARCH,
	_date: new Date(YEAR, MARCH_JS, NumberInt(7)),
	morning: {
		periods: [
			{
				missionId: mission1_customer1._id,
				_startTime: ISODate("2013-03-07T04:00:00Z"),
				_endTime: ISODate("2013-03-07T05:00:00Z")
			}
		]
	},
	afternoon: {
		periods: [
			{
				missionId: mission1_customer1._id,
				_startTime: ISODate("2013-03-07T12:00:00Z"),
				_endTime: ISODate("2013-03-07T13:00:00Z")
			}
		]
	},
	comment: "Comment 1\nComment 2"
});
db.Claim.insert({
	userId: bart._id,
	year: YEAR,
	month: MARCH,
	_date: new Date(YEAR, MARCH_JS, NumberInt(7)),
	missionId: mission1_customer1._id,
	claimType: "MISSION_ALLOWANCE",
	_amount: "4.70"
});
/* 2013/03/12 */
db.Day.insert({
	craId: cra._id,
	userId: bart._id,
	year: YEAR,
	month: MARCH,
	_date: new Date(YEAR, MARCH_JS, NumberInt(12)),
	morning: {
		missionId: holiday._id
	},
	afternoon: {
		missionId: holiday._id
	},
	comment: 'Comment absence...'
});
/* 2013/03/13 */
db.Day.insert({
	craId: cra._id,
	userId: bart._id,
	year: YEAR,
	month: MARCH,
	_date: new Date(YEAR, MARCH_JS, NumberInt(13)),
	morning: {
		missionId: holiday._id
	},
	afternoon: {
		missionId: holiday._id
	},
	comment: 'Comment absence...'
});
/* 2013/03/14 */
db.Day.insert({
	craId: cra._id,
	userId: bart._id,
	year: YEAR,
	month: MARCH,
	_date: new Date(YEAR, MARCH_JS, NumberInt(14)),
	morning: {
		missionId: holiday._id
	},
	afternoon: {
		missionId: holiday._id
	},
	comment: 'Comment absence...'
});

/*
 * Claim
 */
db.Claim.insert({
	userId: bart._id,
	year: YEAR,
	month: MARCH,
	_date: new Date(YEAR, MARCH_JS, NumberInt(4)),
	missionId: mission1_customer1._id,
	claimType: "TAXI",
	comment: "Acheter des donuts",
	_amount: "10.2"
});
db.Claim.insert({
	userId: bart._id,
	year: YEAR,
	month: MARCH,
	_date: new Date(YEAR, MARCH_JS, NumberInt(4)),
	missionId: mission1_customer1._id,
	claimType: "JOURNEY",
	journey: "Springfield",
	comment: "Acheter des donuts",
	_kilometer: "11.5",
	_kilometerAmount: "63.25"
});
db.Claim.insert({
	userId: bart._id,
	year: YEAR,
	month: MARCH,
	_date: new Date(YEAR, MARCH_JS, NumberInt(12)),
	missionId: mission1_customer1._id,
	claimType: "TOLL",
	_amount: "11.5"
});
db.Claim.insert({
	userId: bart._id,
	year: YEAR,
	month: MARCH,
	_date: new Date(YEAR, MARCH_JS, NumberInt(4)),
	missionId: mission1_customer1._id,
	claimType: "TOLL",
	_amount: "17"
});

/* Absence from 11/02/2013 -> 15/02/2013 */
var a = {
	"userId": "5129dc23104cb3b916eff64a",
	"missionId": "5129dc23104cb3b916eff647",
	"startMorning": true,
	"startAfternoon": true,
	"endMorning": true,
	"endAfternoon": true,
	"startDate": 1360537200000,
	"endDate": 1360882800000
}

printjson(bart._id);
