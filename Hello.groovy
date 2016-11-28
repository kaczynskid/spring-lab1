@RestController
class Hello {
    @GetMapping('/hello')
    def say() {
        return { greeting: 'Hello world!' }
    }
}

