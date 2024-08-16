const socket = require("../common/socket")
const ScreenMatcher = function(target, callback) {
    this.status = "pending",
    this.target = target,
    this.callback = callback,
    this.match = function() {
        if (this.status != 'pending') {
            return
        }
        this.status = 'processing'
        socket.send({
            action: "screen-match",
            data: {
                target: this.target
            }  
        }, (data, status) => {
            if (status != "success") {
                return
            }
            if (data && data.targets) {
                this.callback(data.targets)
            }
            this.status = 'pending'
        })
    }
}
exports.match = (target, callback) => {
    const matcher = new ScreenMatcher(
        target, 
        callback
    )
    return matcher.match
}