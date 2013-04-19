var ONE = NumberInt(1);
var YEAR_2010 = NumberInt(2010);
var YEAR_2012 = NumberInt(2013);
var YEAR_2013 = NumberInt(2013);
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

var _2010_JANUARY_1 =   new Date(YEAR_2010, JANUARY_JS, ONE);
var _2013_JANUARY_1 =   new Date(YEAR_2013, JANUARY_JS, ONE);

/**
 * Clean Database
 */
db.Absence.drop();
db.AbsenceDay.drop();
db.Claim.drop();
db.Cra.drop();
db.Customer.drop();
db.Day.drop();
db.HalfDay.drop();
db.Mission.drop();
db.PartTime.drop();
db.Parameter.drop();
db.User.drop();
db.Vehicle.drop();
db.fs.files.drop();
db.fs.chunks.drop();

/**
 * Parameter
 */
db.Parameter.insert({
	_startDate: new Date(YEAR_2012, JANUARY_JS, ONE),
	active: true,
	_car: {
		0: '0.33',
		5: '0.4',
		8: '0.43',
		11: '0.44'
	},
	_motorcycle: {
		"0": "0.3",
		"501": "0.39"
	},
	_zoneAmount: '4.7'
});

/**
 * Customer Genesis
 */
db.Customer.insert({code: '_G_', name: 'Genesis', isGenesis: true });
var genesis = db.Customer.findOne({isGenesis: true});
/**
 * Missions Genesis
 */
db.Mission.insert({customerId: genesis._id, code: 'AV', label: 'Avant vente', allowanceType: 'NONE', missionType: 'pre_sale', isClaimable: true, _startDate: _2010_JANUARY_1});
db.Mission.insert({customerId: genesis._id, code: 'CP', label: 'Congé payé', allowanceType: 'NONE', missionType: 'holiday', absenceType: 'CP', _startDate: _2010_JANUARY_1 });
db.Mission.insert({customerId: genesis._id, code: 'RTTE', label: 'RTT Employeur', allowanceType: 'NONE', missionType: 'holiday', absenceType: 'RTT', _startDate: _2010_JANUARY_1 });
db.Mission.insert({customerId: genesis._id, code: 'RTTS', label: 'RTT Salarié', allowanceType: 'NONE', missionType: 'holiday', absenceType: 'RTT', _startDate: _2010_JANUARY_1 });
db.Mission.insert({customerId: genesis._id, code: 'AE', label: 'Absence exceptionnelle', allowanceType: 'NONE', missionType: 'holiday', absenceType: 'OTHER', _startDate: _2010_JANUARY_1});
db.Mission.insert({customerId: genesis._id, code: 'CSS', label: 'Congé sans solde', allowanceType: 'NONE', missionType: 'holiday', absenceType: 'OTHER', _startDate: _2010_JANUARY_1});
db.Mission.insert({customerId: genesis._id, code: 'TI', label: 'Travaux internes', allowanceType: 'NONE', missionType: 'internal_work', isClaimable: true, _startDate: _2010_JANUARY_1});
db.Mission.insert({customerId: genesis._id, code: 'F', label: 'Formation', allowanceType: 'NONE', missionType: 'internal_work', isClaimable: true, _startDate: _2010_JANUARY_1});
db.Mission.insert({customerId: genesis._id, code: 'IC', label: 'Inter-contrat', allowanceType: 'NONE', missionType: 'internal_work', isClaimable: false, _startDate: _2010_JANUARY_1});
db.Mission.insert({customerId: genesis._id, code: 'TP', label: 'Temps partiel', allowanceType: 'NONE', missionType: 'not_paid', _startDate: _2010_JANUARY_1});
db.Mission.insert({customerId: genesis._id, code: 'MM', label: 'Maladie/Maternité', allowanceType: 'NONE', missionType: 'not_paid', _startDate: _2010_JANUARY_1});
