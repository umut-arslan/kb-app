import axios from 'axios';

export async function subscribe() {
  const notificationPermission = await Notification.requestPermission();
  if (notificationPermission === "granted") {
    const publicKey = await axios.get(`http://localhost:8080/api/push/publicKey`);
    // navigator.serviceWorker.ready.then((serviceWorkerRegistration) => {
    //     const options = {
    //       userVisibleOnly: true,
    //       applicationServerKey: urlB64ToUint8Array(publicKey.data),
    //     };
    //     serviceWorkerRegistration.pushManager.subscribe(options).then((pushSubscription) => {
    //         console.log(pushSubscription);
    //       },
    //     );
    //   }
    // );
    const registration = await navigator.serviceWorker.getRegistration()
    const subscription = await registration?.pushManager.subscribe({
      userVisibleOnly: true,
      applicationServerKey: urlB64ToUint8Array(publicKey.data),
    })
    if (subscription) {
      console.log("subscribed");
      const publicKey = await axios.get(`http://localhost:8080/api/push/subscribe`, {
        params: {
          subscription: JSON.parse(JSON.stringify(subscription)),
        }
      });
    } else {
      console.log("not subscribed");
    }
  }

  function urlB64ToUint8Array(base64String: string) {
    const padding = "=".repeat((4 - (base64String.length % 4)) % 4);
    const base64 = (base64String + padding)
      .replace(/\-/g, "+")
      .replace(/_/g, "/");
    const rawData = window.atob(base64);
    const outputArray = new Uint8Array(rawData.length);
    for (let i = 0; i < rawData.length; ++i) {
      outputArray[i] = rawData.charCodeAt(i);
    }
    return outputArray;
  }
}
