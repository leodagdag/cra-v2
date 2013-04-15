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


var genesis = db.Customer.findOne({isGenesis: true});
var genesisMissions = db.Mission.find({customerId: genesis._id});


/**
 * Cleanning
 */
db.Customer.remove({isGenesis: {$ne: true}});
db.Mission.find({customerId: {$ne: genesis._id}});
db.User.remove();
db.Vehicle.remove();
/**
 * Customer
 * */
db.Customer.insert({code: 'CODE_C_1', name: 'Client 1', isGenesis: false});
db.Customer.insert({code: 'CODE_C_2', name: 'Client 2', isGenesis: false});
db.Customer.insert({code: 'CODE_C_3', name: 'Client 3', isGenesis: false});
var customer1 = db.Customer.findOne({code: 'CODE_C_1'});
var customer2 = db.Customer.findOne({code: 'CODE_C_2'});
var customer3 = db.Customer.findOne({code: 'CODE_C_3'});

/**
 * Mission
 */
db.Mission.insert({customerId: customer1._id, code: 'C1_M1 (REAL)', label: "Cli 1 Mis 1 (réel)", description: 'Description de la mission...', allowanceType: 'REAL', _distance: '43', missionType: 'customer', isClaimable: true, _startDate: new Date(YEAR, JANUARY_JS, ONE), _endDate: new Date(YEAR, APRIL_JS, NumberInt(30))});
db.Mission.insert({customerId: customer1._id, code: 'C1_M2 (ZONE)', label: "Cli 1 Mis 2 (Zone)", description: 'Description de la mission...', allowanceType: 'ZONE', missionType: 'customer', isClaimable: true, _startDate: new Date(YEAR, FEBRUARY_JS, ONE), _endDate: new Date(YEAR, DECEMBER_JS, NumberInt(31))});
db.Mission.insert({customerId: customer2._id, code: 'C2_M1 (REAL)', label: "Cli 2 Mis 1 (Réel)", description: 'Description de la mission...', allowanceType: 'REAL', _distance: '72', missionType: 'customer', isClaimable: true, _startDate: new Date(YEAR, FEBRUARY_JS, ONE), _endDate: new Date(YEAR, APRIL_JS, NumberInt(30))});
db.Mission.insert({customerId: customer3._id, code: 'C3_M1 (ZONE)', label: "Cli 3 Mis 1 (Zone)", description: 'Description de la mission...', allowanceType: 'ZONE', missionType: 'customer', isClaimable: true, _startDate: new Date(YEAR, MARCH_JS, ONE), _endDate: new Date(YEAR, NOVEMBER_JS, NumberInt(30))});
var mission1_customer1 = db.Mission.findOne({customerId: customer1._id, code: 'C1_M1 (REAL)'});
var mission2_customer1 = db.Mission.findOne({customerId: customer1._id, code: 'C1_M2 (ZONE)'});
var mission1_customer2 = db.Mission.findOne({customerId: customer2._id, code: 'C2_M1 (REAL)'});
var mission1_customer3 = db.Mission.findOne({customerId: customer3._id, code: 'C3_M1 (ZONE)'});

/*
 * User
 */
db.User.insert({username: 'lisa', password: '7RT0pNfs3bba6OVJADALHg==', role: 'admin', firstName: 'lisa', lastName: 'Simpson', trigramme: 'LSN', email: 'lisa@simpson.com'});
db.User.insert({username: 'bart-tpl1', password: 'UPqEsTqdRcMBOzNzAu/+3A==', role: 'employee', firstName: 'bart', lastName: 'Simpson Temps Pleins 1', trigramme: 'BSN', email: 'bart@simpson.com',
	affectedMissions: [
		{_startDate: mission1_customer1._startDate, _endDate: mission1_customer1._endDate, missionId: mission1_customer1._id},
		{_startDate: mission2_customer1._startDate, _endDate: mission2_customer1._endDate, missionId: mission2_customer1._id}
	]});
db.User.insert({username: 'bart-tpl2', password: 'ivRfEpsPVrTmmYMZp3R2jw==', role: 'employee', firstName: 'bart', lastName: 'Simpson Temps Pleins 2', trigramme: 'BSN', email: 'bart@simpson.com',
	affectedMissions: [
		{_startDate: mission1_customer1._startDate, _endDate: mission1_customer1._endDate, missionId: mission1_customer1._id},
		{_startDate: mission2_customer1._startDate, _endDate: mission2_customer1._endDate, missionId: mission2_customer1._id}
	]});
db.User.insert({username: 'bart-tpa1', password: '2rB/O7F/etPodCrKvO1AWg==', role: 'employee', firstName: 'bart', lastName: 'Simpson Temps Partiel 1', trigramme: 'BSN', email: 'bart@simpson.com',
	affectedMissions: [
		{_startDate: mission1_customer1._startDate, _endDate: mission1_customer1._endDate, missionId: mission1_customer1._id},
		{_startDate: mission2_customer1._startDate, _endDate: mission2_customer1._endDate, missionId: mission2_customer1._id}
	]});
db.User.insert({username: 'bart-tpa2', password: 'VP93vlWN1p2qcLVYq/hvXw==', role: 'employee', firstName: 'bart', lastName: 'Simpson Temps Partiel 2', trigramme: 'BSN', email: 'bart@simpson.com',
	affectedMissions: [
		{_startDate: mission1_customer1._startDate, _endDate: mission1_customer1._endDate, missionId: mission1_customer1._id},
		{_startDate: mission2_customer1._startDate, _endDate: mission2_customer1._endDate, missionId: mission2_customer1._id}
	]});
db.User.insert({username: 'moe', password: 'fzMzTUwvbdb/xwGUTOwvHA==', role: 'employee', firstName: 'Moe', lastName: 'Szyslak', trigramme: 'MSK', email: 'moe@szyslak.com',
	affectedMissions: [
		{_startDate: mission1_customer2._startDate, _endDate: mission1_customer2._endDate, missionId: mission1_customer2._id},
		{_startDate: mission1_customer3._startDate, _endDate: mission1_customer3._endDate, missionId: mission1_customer3._id}
	]});
db.User.insert({username: 'moe-tpl1', password: 'lBfPWQj8f3NeGIa3B5+SWw==', role: 'employee', firstName: 'Moe', lastName: 'Szyslak Temps Pleins 1', trigramme: 'MSK', email: 'moe@szyslak.com',
	affectedMissions: [
		{_startDate: mission1_customer2._startDate, _endDate: mission1_customer2._endDate, missionId: mission1_customer2._id},
		{_startDate: mission1_customer3._startDate, _endDate: mission1_customer3._endDate, missionId: mission1_customer3._id}
	]});
db.User.insert({username: 'moe-tpl2', password: 'GRB47IMDzwrCDZkunDMRXw==', role: 'employee', firstName: 'Moe', lastName: 'Szyslak Temps Pleins 2', trigramme: 'MSK', email: 'moe@szyslak.com',
	affectedMissions: [
		{_startDate: mission1_customer2._startDate, _endDate: mission1_customer2._endDate, missionId: mission1_customer2._id},
		{_startDate: mission1_customer3._startDate, _endDate: mission1_customer3._endDate, missionId: mission1_customer3._id}
	]});
db.User.insert({username: 'moe-tpa1', password: 'tn6Qc/dtRaO9Iye32h4UOw==', role: 'employee', firstName: 'Moe', lastName: 'Szyslak Temps Partiel 1', trigramme: 'MSK', email: 'moe@szyslak.com',
	affectedMissions: [
		{_startDate: mission1_customer2._startDate, _endDate: mission1_customer2._endDate, missionId: mission1_customer2._id},
		{_startDate: mission1_customer3._startDate, _endDate: mission1_customer3._endDate, missionId: mission1_customer3._id}
	]});
db.User.insert({username: 'moe-tpa2', password: 'pcUHwGQpyWBqwVQhh6c5Pg==', role: 'employee', firstName: 'Moe', lastName: 'Szyslak Temps Partiel 2', trigramme: 'MSK', email: 'moe@szyslak.com',
	affectedMissions: [
		{_startDate: mission1_customer2._startDate, _endDate: mission1_customer2._endDate, missionId: mission1_customer2._id},
		{_startDate: mission1_customer3._startDate, _endDate: mission1_customer3._endDate, missionId: mission1_customer3._id},
	]});
db.User.insert({username: 'seymour', password: '/mQGPJqqQBru0Shwi1/MeQ==', role: 'employee', firstName: 'Seymour', lastName: 'Skinner', trigramme: 'SSR', email: 'seymour@skinner.com', isManager: true});
db.User.insert({username: 'marge', password: '9FC56hi/fYLa0SL3KcCTXw==', role: 'production', firstName: 'marge', lastName: 'Simpson', trigramme: 'MSN', email: 'marge@simpson.com'});
db.User.insert({username: 'ned', password: '9o2q0Ymy//0LjKteNuydlg==', role: 'employee', firstName: 'Ned', lastName: 'Flanders', trigramme: 'NFS',
	email: 'ned@flanders.com', isManager: true});
var ned = db.User.findOne({username: 'ned'});
db.User.insert({username: 'bart', password: '9UFGo/yCqxflJlaVsj9kaw==', role: 'employee', firstName: 'bart', lastName: 'Simpson', trigramme: 'BSN', email: 'bart@simpson.com',
	'managerId': ned._id,
	affectedMissions: [
		{_startDate: mission1_customer1._startDate, _endDate: mission1_customer1._endDate, missionId: mission1_customer1._id},
		{_startDate: mission2_customer1._startDate, _endDate: mission2_customer1._endDate, missionId: mission2_customer1._id}
	]});


db.User.find({role: 'employee'})
	.forEach(function (user) {
		db.Mission.find({customerId: genesis._id}).forEach(function (mission) {
			var endDate = (mission._endDate) ? mission._endDate : null;
			if (user.affectedMissions) {
				user.affectedMissions.push({_startDate: mission._startDate, _endDate: endDate, missionId: mission._id})
			}
		});
		db.User.save(user)
	});
/*
 * Vehicle
 */
var bart = db.User.findOne({username: 'bart'});
db.Vehicle.insert({
	userId: bart._id,
	vehicleType: "car",
	power: NumberInt(5),
	brand: "ALFA_ROMEO",
	matriculation: "AA-123-AA",
	_startDate: new Date(YEAR, MARCH_JS, ONE),
	active: true
});

printjson(bart);
