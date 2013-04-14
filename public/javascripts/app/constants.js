app.constant('YearsConst', [
		{code: 2012, label: 2012},
		{code: 2013, label: 2013},
		{code: 2014, label: 2014},
		{code: 2015, label: 2015}
	])
	.constant('CraYearsConst', [
		{code: '1', label: '2012'},
		{code: '2', label: '2013'},
		{code: '3', label: '2014'},
		{code: '4', label: '2015'}
	])
	.constant('MonthsConst', [
		{'code': 1, 'label': _.str.capitalize(moment('1', 'MM').format('MMMM'))},
		{'code': 2, 'label': _.str.capitalize(moment('2', 'MM').format('MMMM'))},
		{'code': 3, 'label': _.str.capitalize(moment('3', 'MM').format('MMMM'))},
		{'code': 4, 'label': _.str.capitalize(moment('4', 'MM').format('MMMM'))},
		{'code': 5, 'label': _.str.capitalize(moment('5', 'MM').format('MMMM'))},
		{'code': 6, 'label': _.str.capitalize(moment('6', 'MM').format('MMMM'))},
		{'code': 7, 'label': _.str.capitalize(moment('7', 'MM').format('MMMM'))},
		{'code': 8, 'label': _.str.capitalize(moment('8', 'MM').format('MMMM'))},
		{'code': 9, 'label': _.str.capitalize(moment('9', 'MM').format('MMMM'))},
		{'code': 10, 'label': _.str.capitalize(moment('10', 'MM').format('MMMM'))},
		{'code': 11, 'label': _.str.capitalize(moment('11', 'MM').format('MMMM'))},
		{'code': 12, 'label': _.str.capitalize(moment('12', 'MM').format('MMMM'))}
	])
	.constant('RolesConst', {
		'EMPLOYEE': 'employee',
		'PRODUCTION': 'production',
		'ADMIN': 'admin'
	})
	.constant('ClaimTypeConst', [
		{code: 'TAXI', label: 'Taxi'},
		{code: 'PARKING', label: 'Parking'},
		{code: 'TOLL', label: 'Péage'},
		{code: 'RENT_CAR', label: 'Location de voiture'}
	])
	.constant('CarBrandConst', [
		{code: 'ALFA_ROMEO', label: 'Alfa Romeo'},
		{code: 'AUDI', label: 'Audi'},
		{code: 'BMW', label: 'BMW'},
		{code: 'CHRYSLER', label: 'Chrysler'},
		{code: 'CITROËN', label: 'Citroën'},
		{code: 'FERRARI', label: 'Ferrari'},
		{code: 'FIAT', label: 'Fiat'},
		{code: 'FORD', label: 'Ford'},
		{code: 'HONDA', label: 'Honda'},
		{code: 'HYUNDAI', label: 'Hyundai'},
		{code: 'JAGUAR', label: 'Jaguar'},
		{code: 'LADA', label: 'Lada'},
		{code: 'LAMBORGHINI', label: 'Lamborghini'},
		{code: 'LANCIA', label: 'Lancia'},
		{code: 'LAND_ROVER', label: 'Land Rover'},
		{code: 'MASERATI', label: 'Maserati'},
		{code: 'MAZDA', label: 'Mazda'},
		{code: 'MERCEDES', label: 'Mercedes'},
		{code: 'MITSUBISHI', label: 'Mitsubishi'},
		{code: 'NISSAN', label: 'Nissan'},
		{code: 'OPEL', label: 'Opel'},
		{code: 'PEUGEOT', label: 'Peugeot'},
		{code: 'PORSCHE', label: 'Porsche'},
		{code: 'RENAULT', label: 'Renault'},
		{code: 'ROLLS_ROYCE', label: 'Rolls Royce'},
		{code: 'ROVER', label: 'Rover'},
		{code: 'SAAB', label: 'Saab'},
		{code: 'SEAT', label: 'Seat'},
		{code: 'SKODA', label: 'Skoda'},
		{code: 'SUBARU', label: 'Subara'},
		{code: 'SUZUKI', label: 'Suzuki'},
		{code: 'TOYOTA', label: 'Toyota'},
		{code: 'VAUXHALL', label: 'Vauxhall'},
		{code: 'VOLKSWAGEN', label: 'Volkswagen'},
		{code: 'VOLVO', label: 'Volvo'}
	])
	.constant('MotorcycleBrandConst', [
		{code: 'BMW', label: 'BMW'},
		{code: 'DUCATI', label: 'Ducati'},
		{code: 'HONDA', label: 'Honda'},
		{code: 'KAWASAKI', label: 'Kawasaki'},
		{code: 'KTM', label: 'KTM'},
		{code: 'SUZUKI', label: 'Suzuki'},
		{code: 'YAMAHA', label: 'Yamaha'},
		{code: 'GUZZI', label: 'Guzzi'},
		{code: 'PIAGGIO', label: 'PIAGGIO'},
		{code: 'MARTIN', label: 'MARTIN'},
		{code: 'MBK', label: 'MBK'},
		{code: 'VESPA', label: 'VESPA'}
	])
	.constant('VehicleCarPowerConst', [
		{code: 1, label: '1'},
		{code: 2, label: '2'},
		{code: 3, label: '3'},
		{code: 4, label: '4'},
		{code: 5, label: '5'},
		{code: 6, label: '6'},
		{code: 7, label: '7'},
		{code: 8, label: '8'},
		{code: 9, label: '9'},
		{code: 10, label: '10'},
		{code: 11, label: '11'},
		{code: 12, label: '12'},
		{code: 13, label: '13'},
		{code: 14, label: '14'},
		{code: 15, label: '15'}
	])
	.constant('VehicleMotorcyclePowerConst', [
		{code: 0, label: 'De 0 à 500'},
		{code: 501, label: 'Plus de 500'}
	])
	.constant('AbsenceTypeConst', [
		{code: 'all', label: 'Tout'},
		{code: 'cp', label: 'CP'},
		{code: 'rtt', label: 'RTT'}
	]);


