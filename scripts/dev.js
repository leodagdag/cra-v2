var MARCH_JS = 2;
var MARCH = 3;
db.User.drop();
db.Cra.drop();
db.Day.drop();
db.HalfDay.drop();
db.Absence.drop();
db.PartTime.drop();
db.Claim.drop();
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
db.Mission.insert({customerId: customer1._id, code: 'C1_M1', description: 'Description de la mission...', missionType: 'customer', isClaim: true, _startDate: new Date(2013, 0, 1), _endDate: new Date(2013, 4, 31)});
db.Mission.insert({customerId: customer1._id, code: 'C1_M2', description: 'Description de la mission...', missionType: 'customer', isClaim: true, _startDate: new Date(2013, 1, 1), _endDate: new Date(2013, 4, 31)});
db.Mission.insert({customerId: genesis._id, code: 'AV', description: 'Avant vente', missionType: 'pre-sale', isClaim: true, _startDate: new Date(2013, 0, 1)});
db.Mission.insert({customerId: genesis._id, code: 'CP', description: 'Congé payé', missionType: 'holiday', absenceType: 'CP', _startDate: new Date(2013, 0, 1) });
db.Mission.insert({customerId: genesis._id, code: 'RTTE', description: 'RTT Employeur', missionType: 'holiday', absenceType: 'RTT', _startDate: new Date(2013, 0, 1) });
db.Mission.insert({customerId: genesis._id, code: 'RTTS', description: 'RTT Salarié', missionType: 'holiday', absenceType: 'RTT', _startDate: new Date(2013, 0, 1) });
db.Mission.insert({customerId: genesis._id, code: 'AE', description: 'Absence exceptionnelle', missionType: 'holiday', absenceType: 'CP', _startDate: new Date(2013, 0, 1) });
db.Mission.insert({customerId: genesis._id, code: 'TP', description: 'Temps partiel', missionType: 'not-paid', _startDate: new Date(2013, 0, 1)});
var mission_customer1 = db.Mission.findOne({customerId: customer1._id, code: 'C1_M1'});
var mission_customer2 = db.Mission.findOne({customerId: customer1._id, code: 'C1_M2'});
var pre_sale = db.Mission.findOne({customerId: genesis._id, missionType: 'pre-sale' });
var holiday = db.Mission.findOne({customerId: genesis._id, code: 'CP', missionType: 'holiday'});
var rtte = db.Mission.findOne({customerId: genesis._id, code: 'RTTE', missionType: 'holiday'});
var rtts = db.Mission.findOne({customerId: genesis._id, code: 'RTTS', missionType: 'holiday'});
var ae = db.Mission.findOne({customerId: genesis._id, code: 'AE', missionType: 'holiday'});
var part_time = db.Mission.findOne({customerId: genesis._id, missionType: 'not-paid' });

/*
 * User
 */
db.User.insert({username: 'lisa', password: 'lisa', role: 'admin', firstName: 'lisa', lastName: 'Simpson', trigramme: 'LSN', email: 'lisa@simpson.com'});
db.User.insert({username: 'bart', password: 'bart', role: 'employee', firstName: 'bart', lastName: 'Simpson', trigramme: 'BSN', email: 'bart@simpson.com',
	affectedMissions: [
		{_startDate: mission_customer1._startDate, _endDate: mission_customer1._endDate, missionId: mission_customer1._id},
		{_startDate: mission_customer2._startDate, _endDate: mission_customer2._endDate, missionId: mission_customer2._id},
		{_startDate: pre_sale._startDate, _endDate: pre_sale._endDate, missionId: pre_sale._id},
		{_startDate: holiday._startDate, _endDate: holiday._endDate, missionId: holiday._id},
		{_startDate: rtte._startDate, _endDate: rtte._endDate, missionId: rtte._id},
		{_startDate: rtts._startDate, _endDate: rtts._endDate, missionId: rtts._id},
		{_startDate: ae._startDate, _endDate: ae._endDate, missionId: ae._id},
		{_startDate: part_time._startDate, _endDate: part_time._endDate, missionId: part_time._id}
	]});
db.User.insert({username: 'homer', password: 'homer', role: 'employee', firstName: 'homer', lastName: 'Simpson', trigramme: 'HSN', email: 'homer@simpson.com'});
db.User.insert({username: 'marge', password: 'marge', role: 'production', firstName: 'marge', lastName: 'Simpson', trigramme: 'MSN', email: 'marge@simpson.com'});
var bart = db.User.findOne({username: 'bart'});

/*
 * Cra
 */
db.Cra.insert({year: NumberInt(2013), month: NumberInt(MARCH), userId: bart._id, comment: 'Commentaire cra...', isValidated: false});
var cra = db.Cra.findOne({year: 2013, month: MARCH});

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
	_date: new Date(cra.year, MARCH_JS, NumberInt(1)),
	year: NumberInt(cra.year),
	month: NumberInt(cra.month),
	morning: {
		missionId: mission_customer1._id
	},
	afternoon: {
		missionId: mission_customer1._id
	},
	comment: 'Comment day ...'
});
/* 2013/03/05 */
db.Day.insert({
	craId: cra._id,
	_date: new Date(cra.year, MARCH_JS, NumberInt(5)),
	year: NumberInt(cra.year),
	month: NumberInt(cra.month),
	morning: {
		missionId: mission_customer2._id
	},
	afternoon: {
		missionId: mission_customer2._id
	}
});
/* 2013/03/06 */
db.Day.insert({
	craId: cra._id,
	_date: new Date(cra.year,MARCH_JS, NumberInt(6)),
	year: NumberInt(cra.year),
	month: NumberInt(cra.month),
	morning: {
		missionId: pre_sale._id
	},
	afternoon: {
		missionId: pre_sale._id
	},
	comment: 'Comment day ...'
});
/* 2013/03/07 */
db.Day.insert({
	craId: cra._id,
	_date: new Date(cra.year,MARCH_JS, NumberInt(7)),
	year: NumberInt(cra.year),
	month: NumberInt(cra.month),
	morning: {
		periods: [
			{
				missionId: mission_customer1._id,
				_startTime: ISODate("2013-03-07T04:00:00Z"),
				_endTime: ISODate("2013-03-07T05:00:00Z")
			}
		]
	},
	afternoon: {
		periods: [
			{
				missionId: mission_customer1._id,
				_startTime: ISODate("2013-03-07T12:00:00Z"),
				_endTime: ISODate("2013-03-07T13:00:00Z")
			}
		]
	},
	comment: "Comment 1\nComment 2"
});
/* 2013/03/12 */
db.Day.insert({
	craId: cra._id,
	_date: new Date(cra.year, MARCH_JS, NumberInt(12)),
	year: NumberInt(cra.year),
	month: NumberInt(cra.month),
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
	_date: new Date(cra.year, MARCH_JS, NumberInt(13)),
	year: NumberInt(cra.year),
	month: NumberInt(cra.month),
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
	_date: new Date(cra.year, MARCH_JS, NumberInt(14)),
	year: NumberInt(cra.year),
	month: NumberInt(cra.month),
	morning: {
		missionId: holiday._id
	},
	afternoon: {
		missionId: holiday._id
	},
	comment: 'Comment absence...'
});
/* Qbsence from 11/02/2013 -> 15/02/2013 */
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
