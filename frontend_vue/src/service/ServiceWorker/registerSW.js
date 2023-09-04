if ('serviceWorker' in navigator) {
  console.log("Zeile 2 wurde erreicht");
  window.addEventListener('load', () => {
    console.log("Service Worker registered");
    navigator.serviceWorker.register('/sw.js', {scope: '/'})
  })
} else {
  console.log("Ich bin im else Fall brother");
}
