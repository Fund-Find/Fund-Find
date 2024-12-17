import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
    plugins: [react()],
})

// import { defineConfig } from 'vite'
// import react from '@vitejs/plugin-react'
// import path from 'path'

// export default defineConfig({
//     plugins: [react()],
//     resolve: {
//         alias: {
//             '@': path.resolve(__dirname, './src'),
//         },
//     },
// })

// ------------------------------------------------------

// import { defineConfig } from 'vite'
// import react from '@vitejs/plugin-react'
// import tsconfigPaths from 'vite-tsconfig-paths'

// export default defineConfig({
//     plugins: [react(), tsconfigPaths()],
// })
