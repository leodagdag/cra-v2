module.exports = function(grunt) {

	// Project configuration.
	grunt.initConfig({
		pkg: grunt.file.readJSON('package.json'),
		clean: ['<%= pkg.build.path %>', '<%= pkg.dist.path + pkg.dist.file %>'],
		concat: {
			options: {
				// define a string to put between each file in the concatenated output
				separator: ';'
			},
			dist: {
				// the files to concatenate
				src: ['<%= pkg.src %>'],
				// the location of the resulting JS file
				dest: '<%= pkg.build.path + pkg.build.file %>'
			}
		},
		uglify: {
			options: {
				banner: '/*! <%= pkg.name %> | <%= pkg.version %> | <%= grunt.template.today("yyyy-mm-dd HH:MM:ss") %> */\n'
			},
			dist: {
				files: {
					'<%= pkg.dist.path + pkg.dist.file %>': ['<%= concat.dist.dest %>']
				}
			}
		},
		copy:{
			main:{
				files:[
					{src: ['<%= concat.dist.dest %>'], dest: '<%= pkg.dist.path + pkg.dist.file %>', filter: 'isFile'}
				]
			}
		}
	});
	grunt.loadNpmTasks('grunt-contrib-clean');
	grunt.loadNpmTasks('grunt-contrib-concat');
	grunt.loadNpmTasks('grunt-contrib-uglify');
	grunt.loadNpmTasks('grunt-contrib-copy');

	// Default task(s).
	grunt.registerTask('default', ['clean']);
	grunt.registerTask('build', ['clean', 'concat']);
	grunt.registerTask('dist', ['clean', 'concat', 'copy']);

};
