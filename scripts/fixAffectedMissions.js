var _2013_JANUARY_1 =   new Date(NumberInt(2013), NumberInt(0), NumberInt(1));

db.User.find({'affectedMissions': {$exists: true}}).forEach(function(user) {

	for(var i = 0; i < user.affectedMissions.length -1; i++){
		if(!user.affectedMissions[i].allowanceType){
			user.affectedMissions[i].allowanceType = 'NONE';
		}
		if(!user.affectedMissions[i]._startDate){
			user.affectedMissions[i]._startDate =_2013_JANUARY_1;
		}
	}
	db.User.save(user);
});