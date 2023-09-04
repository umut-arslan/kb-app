self.addEventListener('push',function(e){
  let data = e.data.json();
  console.log(data);

  const options = {
    body: data.body, // You can add the body from your data
  };

  e.waitUntil(
    self.registration.showNotification(data.title, options).then(() => {
      console.log("Notification shown successfully");
    }));
})

self.addEventListener('load', ()=>{
  console.log("I got loaded")
})

self.addEventListener('fetching', ()=>{
  console.log("I got loaded")
})
